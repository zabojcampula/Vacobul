package com.zabojcampula.vacobul;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JOptionPane;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zabojcampula.drive.GooDrive;
import com.zabojcampula.drive.IRemoteDrive;

class VacobulData {

	private static Logger log = Logger.getGlobal();

	private List<VacobulElement> data;
	private List<VacobulElement> data2;
	private String activeFileName;
	private DataState dataState;
	private int currentElement = -1;
	IRemoteDrive drive = null;

	VacobulData(String fileName) {
		data = new ArrayList<>();
		data2 = new ArrayList<>();
		dataState = new DataState();
		activeFileName = fileName;
		splitToBuckets(load(fileName));
	}
	
	public String getEn(String cz) {
		currentElement = 0;
		for (VacobulElement e: data2) {
			if (e.czWord.equals(cz))
				return e.enWord;
			currentElement++;
		}
		currentElement = -1;
		return null;
	}
	public String getCz(String en) {
		currentElement = 0;
		for (VacobulElement e: data2) {
			if (e.enWord.equals(en))
				return e.czWord;
			currentElement++;
		}
		currentElement = -1;
		return null;
	}
	
	public VacobulElement getElement(String en) {
		currentElement = 0;
		for (VacobulElement e: data2) {
			if (e.enWord.equals(en))
				return e;
			currentElement++;
		}
		currentElement = -1;
		return null;
	}
	
	void switchData() {
		 List<VacobulElement> tmp = data;
		 data = data2;
		 data2 = tmp;
	}
	
	VacobulElement getRandomElement() {
		currentElement = -1;
		if (data == null || data.size() + data2.size() == 0)
			return null;

		VacobulElement selected = null;
		while (selected == null) {
			if (data.size() == 0) {
				switchData();
			}
			Random rand = new Random(); 
			currentElement = rand.nextInt(data.size());

			selected = data.get(currentElement);
			data2.add(selected);
			data.remove(currentElement);
			selected.bucketno = 2;
			if (selected.probability < rand.nextInt(10)) {
				selected.probability++;
				selected = null;
			}
		}
		dataState.elementExamined();
		return selected;
	}

	private static List<VacobulElement> load(String fileName) {
		ObjectMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		List<VacobulElement> allElements;
		try {
			allElements = mapper.readValue(new File(fileName), new TypeReference<>() {
			});
		} catch (IOException e) {
			log.warning("err - json error read " + e);
			JOptionPane.showMessageDialog(null, "Disk read error from file. " + e.getMessage(), "InfoBox: " + "Vacobul problem", JOptionPane.INFORMATION_MESSAGE);
			return Collections.emptyList();
		}
		return allElements;
	}

	private void splitToBuckets(List<VacobulElement> allElements) {
		Map<Boolean, List<VacobulElement>> m = allElements.stream().collect(Collectors.partitioningBy(e -> e.bucketno == 1));
		data = m.get(true) == null ? new ArrayList<>() : m.get(true);
		data2 = m.get(false) == null ? new ArrayList<>() : m.get(false);
		currentElement = -1;
	}
	
	private void saveJson() throws IOException {
		List<VacobulElement> allElements = new ArrayList<>();
		allElements.addAll(data);
		allElements.addAll(data2);
		ObjectMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		String json = mapper.writeValueAsString(allElements);
		try (FileOutputStream fos = new FileOutputStream(activeFileName);
			 Writer out = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
			out.write(json);
		}
	}

	public void saveIfNeeded() throws IOException {
		if (dataState.isWorthWrite()) {
			save();
		}
	}

	public void save() throws IOException {
		if (dataState.isDirty()) {
			saveJson();
			dataState.reset();
		}
	}
	
	public void addNewElement(VacobulElement e) {
		data.add(e);
		currentElement = data.size() - 1;
		dataState.elementAdded();
	}

	public void updateElement(VacobulElement e) {
		data2.get(currentElement).czWord = e.czWord;
		data2.get(currentElement).examples = e.examples;
		data2.get(currentElement).enWord = e.enWord;
		dataState.elementAdded();
	}
	
	public int updateProbability(int delta, String en) {
		int newprob = 0;
		for (VacobulElement e: data2) {
			if (e.enWord.equals(en)) {
				newprob = delta + e.probability;
				if (newprob > 10)
					newprob = 10;
				e.probability = newprob;
				e.lastChecked = Instant.now();
				dataState.elementExamined();
				break;
			}
		}
		return newprob;
	}

	public String stats() {
		return String.format("File: %s elements: %d", activeFileName, data.size() + data2.size());
	}

	public Object getDataStatus() {
		return dataState.toString() + "     " + dataStatistics();
	}

	private String dataStatistics() {
		return String.format("bucket1:%-3d    bucket2:%-3d", data.size(), data2.size());
	}

	public void sync() {

		if (drive == null) {
			try {
				drive = new GooDrive("Vacobul");
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}

		String remoteFileName = activeFileName + ".remote";
		try {
			drive.downloadFile(activeFileName, remoteFileName);
		} catch (NoSuchElementException e) {
			System.out.println("remote file not present just upload");
			try {
				drive.uploadFile(activeFileName);
				System.out.println("upload ok");
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			return;

		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		System.out.println("download ok");


		try {
			save();
			List<VacobulElement> remote = load(remoteFileName);
			List<VacobulElement> local = load(activeFileName);

			List<VacobulElement> mergedList = compareDictionaries(remote, local);
			splitToBuckets(mergedList);
			saveJson();
			drive.uploadFile(activeFileName);

			System.out.println("upload ok");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static <T> Predicate<T> distinctByEnWord(Function<? super T, ?> keyExtractor) {
		Set<Object> seen = ConcurrentHashMap.newKeySet();
		return t -> seen.add(keyExtractor.apply(t));
	}

	private List<VacobulElement> compareDictionaries(List<VacobulElement> remote, List<VacobulElement> local) {

		System.out.println("remote size " + remote.size());
		System.out.println("local size " + local.size());


		List<VacobulElement> mergedList = Stream.concat(remote.stream(), local.stream())
				                                .sorted((e1,e2)  -> e2.lastChecked.compareTo(e1.lastChecked))
												.filter(distinctByEnWord(VacobulElement::getEnWord))
												.collect(Collectors.toList());
		//mergedList.stream().forEach(System.out::println);
		return mergedList;
	}
}


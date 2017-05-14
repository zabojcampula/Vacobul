package com.zabojcampula.vacobul;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JOptionPane;

class VacobulElement {
	private static final String ITEMSEP = "|";
	public String enWord;
	public String czWord;
	public int bucketno;
	public int probability;
	public String examples;

	public VacobulElement() {
	}
	
	public VacobulElement(String en, String cz) {
		this(en, cz, "");
	}
	public VacobulElement(String en, String cz, String examples) {
		enWord = en;
		czWord = cz;
		bucketno = 1;
		probability = 8;
		this.examples = examples;
	}
	
	public VacobulElement(String en, String cz, int bucketno_, int probability_, String examples_) {
		enWord = en;
		czWord = cz;
		bucketno = bucketno_;
		probability = probability_;
		examples = examples_;
	}
	
	public String toLine() {
		StringBuffer b = new StringBuffer();
		b.append(enWord);
		b.append(ITEMSEP);
		b.append(czWord);
		b.append(ITEMSEP);
		b.append(bucketno);
		b.append(ITEMSEP);
		b.append(probability);
		b.append(ITEMSEP);
		b.append(examples);
		return b.toString();
	}
	
	void fromLine(String line) {
		
	}
}

public class VacobulData {

	private ArrayList<VacobulElement> data;
	private ArrayList<VacobulElement> data2;
	private String activeFileName;
	private int currentElement = -1;

	public VacobulData() {
		data = new ArrayList<VacobulElement>();
		data2 = new ArrayList<VacobulElement>();
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
		 ArrayList<VacobulElement> tmp = data;
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
		return selected;
	}
	
	public void load(String fileName)
	{
		currentElement = -1;
		FileInputStream fis;
		try {
			fis = new FileInputStream(fileName);
		    Scanner scanner = new Scanner(fis, "UTF-8");
		    while (scanner.hasNextLine()) {
		    	String[] splitString = (scanner.nextLine().split("\\|"));
		    	String examples = splitString.length > 4 ? splitString[4] : "";
		    	if (splitString.length > 3) {
		    		int bucketno = Integer.parseInt(splitString[2]);
		    		VacobulElement v = new VacobulElement(splitString[0], splitString[1], bucketno, Integer.parseInt(splitString[3]), examples);
		    		if (bucketno == 1) {
			    	    data.add(v);	
		    		} else {
		    			data2.add(v);
		    		}
		    	}		    		
		    	else if (splitString.length > 1) {
		    		this.data.add(new VacobulElement(splitString[0], splitString[1]));
		    	}
		    	
		    }
		    scanner.close();
			fis.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Disk read error from file. " + e.getMessage(), "InfoBox: " + "Vacobul problem", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		activeFileName = fileName;
	}
	
	void writeone(Writer out,  ArrayList<VacobulElement> d) throws IOException {
		for (VacobulElement e: d) {
			out.write(e.toLine());
			out.write(System.getProperty("line.separator"));
		}
	}
	
	public void save() throws IOException {
		FileOutputStream fos = null;
		if (activeFileName == null)
			activeFileName = "dictionary.txt";
		try { 
			fos = new FileOutputStream(activeFileName);
			Writer out = new OutputStreamWriter(fos, "UTF-8");
			writeone(out, data);
			writeone(out, data2);
			out.flush();
			fos.close();
		} catch(IOException e) {
			fos.close();
			throw e;
		}
		fos.close();
	}
	
	public void addNewElement(VacobulElement e) {
		data.add(e);
		currentElement = data.size() - 1;
	}

	public void updateElement(VacobulElement e) {
		data2.get(currentElement).czWord = e.czWord;
		data2.get(currentElement).examples = e.examples;
		data2.get(currentElement).enWord = e.enWord;
	}
	
	public int updateProbability(int delta, String en) {
		int newprob = 0;
		for (VacobulElement e: data2) {
			if (e.enWord.equals(en)) {
				newprob = delta + e.probability;
				if (newprob > 10)
					newprob = 10;
				e.probability = newprob;
				break;
			}
		}
		return newprob;
	}

}


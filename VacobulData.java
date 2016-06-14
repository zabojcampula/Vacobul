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

	public VacobulElement() {
	}
	
	public VacobulElement(String en, String cz) {
		enWord = en;
		czWord = cz;
		bucketno = 1;
		probability = 8;
	}
	
	public VacobulElement(String en, String cz, int bucketno_, int probability_) {
		enWord = en;
		czWord = cz;
		bucketno = bucketno_;
		probability = probability_;
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
		    	VacobulElement v = new VacobulElement();
		    	String[] splitString = (scanner.nextLine().split("\\|"));
		    	if (splitString.length > 3) {
		    		int bucketno = Integer.parseInt(splitString[2]);
		    		if (bucketno == 1) {
			    	    data.add(new VacobulElement(splitString[0], splitString[1], bucketno, Integer.parseInt(splitString[3])));	
		    		} else {
		    			data2.add(new VacobulElement(splitString[0], splitString[1], bucketno, Integer.parseInt(splitString[3])));
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
		data.get(currentElement).czWord = e.czWord;
		data.get(currentElement).enWord = e.enWord;
	}
	
	public void updateProbability(int delta, String en) {
		for (VacobulElement e: data2) {
			if (e.enWord.equals(en)) {
				int newprob = delta + e.probability;
				if (newprob > 10)
					newprob = 10;
				else if (newprob < 1)
					newprob = 1;
				e.probability = newprob;
				break;
			}
		}
	}

}


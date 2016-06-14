package com.zabojcampula.vacobul;
import java.io.IOException;

import javax.swing.JOptionPane;


public class VacobulActions {
	
	Vacobul gui;
	VacobulData data;
	
	VacobulActions(Vacobul g) {
		gui = g;
		data = new VacobulData();
		data.load("dictionary.txt");
	}
	
	void newAction() throws IOException {
		VacobulElement e = new VacobulElement(gui.getEnWord(), gui.getCzWord());
		data.addNewElement(e);
		data.save();
		JOptionPane.showMessageDialog(null, "New word added ", "InfoBox: " + "Vacobul Info", JOptionPane.INFORMATION_MESSAGE);
		
	}
	private void updateAction() throws IOException {
		VacobulElement e = new VacobulElement(gui.getEnWord(), gui.getCzWord());
		data.updateElement(e);
		data.save();
		JOptionPane.showMessageDialog(null, "Word updated ", "InfoBox: " + "Vacobul Info", JOptionPane.INFORMATION_MESSAGE);
		
	}
	
	void nextAction() throws IOException {
		VacobulElement e = data.getRandomElement();
		if (e == null)
		{
			gui.setEnWord("");
		} else {
			gui.setEnWord(e.enWord);
			gui.setStatus(" Level:" + e.probability + "  Bucket:" + e.bucketno);
		}
		gui.setCzWord("");
		data.save();
	}

	void revealAction() throws IOException {
		gui.setCzWord(data.getCz(gui.getEnWord()));
	}
	
	void knownAction() throws IOException {
		data.updateProbability(-2, gui.getEnWord());
		data.save();
	}
	
	void unknownAction() throws IOException {
		data.updateProbability(2, gui.getEnWord());
		data.save();
	}
	
	void commonHandler(String a) {
		try {
			switch(a) {
				case "know":
					knownAction();
					break;
				case "unknown":
					unknownAction();
					break;
				case "next":
					nextAction();
					break;
				case "new":
					newAction();
					break;
				case "reveal":
					revealAction();
					break;
				case "update":
					updateAction();
					break;
			}
		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Disk error " + e.getMessage(), "InfoBox: " + "Vacobul problem", JOptionPane.INFORMATION_MESSAGE);
		}
	}


}

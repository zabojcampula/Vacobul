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
		VacobulElement e = new VacobulElement(gui.getEnWord(), gui.getCzWord(), gui.getExamples());
		data.addNewElement(e);
		data.save();
		JOptionPane.showMessageDialog(null, "New word added ", "InfoBox: " + "Vacobul Info", JOptionPane.INFORMATION_MESSAGE);
		
	}
	private void updateAction() throws IOException {
		VacobulElement e = new VacobulElement(gui.getEnWord(), gui.getCzWord(), gui.getExamples());
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
			gui.setStatus(" Level:" + e.probability);
		}
		gui.setCzWord("");
		gui.setExamples("");
		data.save();
	}

	void revealAction() throws IOException {
		VacobulElement e = data.getElement(gui.getEnWord());
		gui.setCzWord(e.czWord);
		gui.setExamples(e.examples);
	}
	
	void changeProbability(int delta) throws IOException {
		int newprob = data.updateProbability(delta, gui.getEnWord());
		gui.setStatus(" Level:" + newprob);
		data.save();
	}
	
	void commonHandler(String a) {
		try {
			switch(a) {
				case "know":
					changeProbability(-2);
					break;
				case "KNOW":
					changeProbability(-15);
					break;
				case "unknown":
					changeProbability(2);
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

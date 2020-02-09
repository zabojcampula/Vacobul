package com.zabojcampula.vacobul;
import com.zabojcampula.drive.GooDrive;
import com.zabojcampula.drive.IRemoteDrive;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.swing.JOptionPane;


public class VacobulActions extends WindowAdapter {
	
	Vacobul gui;
	VacobulData data;
	
	VacobulActions(Vacobul g, VacobulData data_) {
		gui = g;
		data = data_;
		gui.setStatus(constructStatus(0));
	}
	
	void newAction() throws IOException {
		VacobulElement e = new VacobulElement(gui.getEnWord(), gui.getCzWord(), gui.getExamples());
		data.addNewElement(e);
		data.saveIfNeeded();
		JOptionPane.showMessageDialog(null, "New word added ", "InfoBox: " + "Vacobul Info", JOptionPane.INFORMATION_MESSAGE);
		
	}
	private void updateAction() throws IOException {
		VacobulElement e = new VacobulElement(gui.getEnWord(), gui.getCzWord(), gui.getExamples());
		data.updateElement(e);
		data.saveIfNeeded();
		JOptionPane.showMessageDialog(null, "Word updated ", "InfoBox: " + "Vacobul Info", JOptionPane.INFORMATION_MESSAGE);
		
	}
	
	void nextAction() throws IOException {
		VacobulElement e = data.getRandomElement();
		if (e == null)
		{
			gui.setEnWord("");
		} else {
			gui.setEnWord(e.enWord);
			gui.setStatus(constructStatus(e.probability));
		}
		gui.setCzWord("");
		gui.setExamples("");
		data.saveIfNeeded();
	}

	void revealAction() {
		VacobulElement e = data.getElement(gui.getEnWord());
		gui.setCzWord(e.czWord);
		gui.setExamples(e.examples);
	}

	void showStats() {
        new StatsForm(data.stats());
	}

	void changeProbability(int delta) throws IOException {
		int newprob = data.updateProbability(delta, gui.getEnWord());
		gui.setStatus(constructStatus(newprob));
		data.saveIfNeeded();
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
				case "stats":
					showStats();
					break;
				case "sync":
					sync();
					break;
			}
		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Disk error " + e.getMessage(), "InfoBox: " + "Vacobul problem", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	@Override
	public void windowClosing(WindowEvent e) {
		try {
			data.save();
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(null,
					"Cannot write data. " + ex.getMessage(),
					"InfoBox: " + "Vacobul problem",
					JOptionPane.INFORMATION_MESSAGE);
		}
		e.getWindow().dispose();
	}

	private String constructStatus(int probability) {
		return String.format(" Level:%-3d         %s" , probability, data.getDataStatus()) ;
	}

	private void sync() {
		data.sync();
	}


}

package com.zabojcampula.vacobul;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextArea;

public class Vacobul extends JFrame implements ActionListener {

	JTextField enWord;
	JTextField czWord;
	JLabel status;
	JTextArea area;
	VacobulActions actions;
	
	Vacobul (String dictionaryFileName) {
		VacobulData data = new VacobulData(dictionaryFileName);
		CreateUI();
		actions = new VacobulActions(this, data);
		addWindowListener(actions);

	}

	public String getEnWord() {
		return enWord.getText();
	}
	public void setEnWord(String enWord) {
		this.enWord.setText(enWord);
	}
	public String getCzWord() {
		return czWord.getText();
	}
	public void setCzWord(String czWord) {
		this.czWord.setText(czWord);
	}
	public void setStatus(String label) {
		this.status.setText(label);
	}
	
	public String getExamples() {
		return area.getText().replaceAll("\n", "<br>");
	}

	public void setExamples(String examples) {
		if (examples == null) {
			area.setText("");
		} else {
			area.setText(examples.replaceAll("<br>", "\n"));
		}
	}

	private JButton addButton(String text, JPanel panel) {
		JButton b;
		b = new JButton(text);
		b.setActionCommand(text);
		b.addActionListener(this);
		panel.add(b);
		return b;
	}
	
	void CreateUI() {
		
		BoxLayout layout = new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS);
		getContentPane().setLayout(layout);
		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();
		JPanel panel3 = new JPanel();
		JPanel panel4 = new JPanel();
		add(panel1);
		add(panel2);
		add(panel3);
		add(panel4);
		panel1.setLayout(new GridLayout());
		panel3.setLayout(new GridLayout());
		panel2.setLayout(new GridLayout());
		panel4.setLayout(new GridLayout());
		
		enWord = new JTextField();
		czWord  = new JTextField();
		panel1.add(enWord);
		panel1.add(czWord);
		status = new JLabel("");

		panel4.add(status);

		addButton("KNOW", panel3);
		addButton("know", panel3);
		addButton("unknown", panel3);
		addButton("reveal", panel3);
		addButton("next", panel3);
		addButton("new", panel3);
		addButton("update", panel3);
		addButton("stats", panel3);
		addButton("sync", panel3);

		area = new JTextArea(7, 5);
		panel2.add(area);


		pack();
	}

	public void actionPerformed(ActionEvent e) {
        actions.commonHandler(e.getActionCommand());
	}
	
	//public static 

	public static void main(String[] args) {

		String dictionaryFileName = args.length > 0 ? args[0] : "dictionary.json";
		java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Vacobul(dictionaryFileName).setVisible(true);
            }
        });
	}

}
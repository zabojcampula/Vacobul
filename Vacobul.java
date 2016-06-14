package com.zabojcampula.vacobul;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


@SuppressWarnings("serial")
public class Vacobul extends JFrame implements ActionListener {

	JTextField enWord;
	JTextField czWord;
	JLabel status;
	VacobulActions actions;
	
	Vacobul () {
        actions = new VacobulActions(this);
		CreateUI();
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
		add(panel1);
		add(panel2);
		add(panel3);
		panel1.setLayout(new GridLayout());
		panel2.setLayout(new GridLayout());
		panel3.setLayout(new GridLayout());
		
		enWord = new JTextField();
		czWord  = new JTextField();
		panel1.add(enWord);
		panel1.add(czWord);
		status = new JLabel("hulala");
		panel3.add(status);

		addButton("know", panel2);
		addButton("unknow", panel2);
		addButton("reveal", panel2);
		addButton("next", panel2);
		addButton("new", panel2);
		addButton("update", panel2);
			
		pack();
	}

	public void actionPerformed(ActionEvent e) {
        actions.commonHandler(e.getActionCommand());
	}
	
	//public static 

	public static void main(String[] args) {
		java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Vacobul().setVisible(true);
            }
        });
	}

}

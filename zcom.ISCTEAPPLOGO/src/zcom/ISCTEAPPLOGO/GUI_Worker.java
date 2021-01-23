package zcom.ISCTEAPPLOGO;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GUI_Worker extends Thread {

	private String search;
	private boolean working = false;
	private int tasks_done = 0;
	private JFrame result_frame = new JFrame("Worker");
	private JLabel details = new JLabel(search);
	private JLabel status = new JLabel("Working: " + working);
	private JLabel status1 = new JLabel("Tasks done: " + tasks_done);
	private JPanel status_panel = new JPanel();
	private JPanel details_panel = new JPanel();
	
	public GUI_Worker(String search) {
		this.search = search;
		result_frame.setTitle("Worker: " + search);
	}
	
	@Override
	public void run() {
		
		result_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		result_frame.setSize(300, 150);
		result_frame.setLocationRelativeTo(null);
		details_panel.add(details);
		status_panel.add(status, BorderLayout.NORTH);
		status_panel.add(status1, BorderLayout.SOUTH);
		result_frame.add(status_panel);
		result_frame.add(details_panel, BorderLayout.SOUTH);
		status_panel.setBackground(Color.RED);
		result_frame.setVisible(true);
		
	}
	
	public void setStatus(boolean b) {
		this.working = b;
		status.setText("Working: " + working);
		if(working) {
		status_panel.setBackground(Color.GREEN);
		}else {
			status_panel.setBackground(Color.RED);
		}
	}
	
	public void setTasks() {
		this.tasks_done++;
		status1.setText("Tasks done: " + tasks_done);
	}
	
	
}

package zcom.ISCTEAPPLOGO;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;

import data_structs.Points_Name;
import data_structs.SerializedBuff_Image_Name;
import data_structs.WorkPackage;
import data_structs.WorkPackageResponse;
import data_structs.WorkerLogin;

public class Worker{

	private Socket s;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private String search;
	private GUI_Worker gui_worker;
	
	public Worker(String IP, int PORT, String search) {
		if(search.equals("Simples")) {
			search = "Procura " + search;
		}else {
			search = "Procura " + search + "º";
		}
		this.search = search;
		Connect(IP,PORT,search);
		gui_worker = new GUI_Worker(search);
		gui_worker.start();
		Communicate();
	}
	
	private void Communicate() {
		Object obj;
		while(true) {
			try {
				obj = ois.readObject();
				if(obj instanceof WorkPackage)
					DealWithPackage((WorkPackage) obj);
				
			} catch (ClassNotFoundException | IOException e) {  
			
			break;}
		}
		
	}

	private void DealWithPackage(WorkPackage wp) {
		gui_worker.setStatus(true);
		LinkedList<Points_Name> pn = new LinkedList<Points_Name>();
		BufferedImage subimage = wp.getImages_List_serialized().getSubImage();
		pn.clear();
		pn.addAll(getPoints(wp.getImages_List_serialized().getImages_list(), subimage));
		System.out.println("Worker received image");
		for (Points_Name points_Name : pn) {
			System.out.println(points_Name.getPoints().toString());
		}
		try {
			oos.writeObject(new WorkPackageResponse(wp.getIdClient(), pn, search));
			oos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			oos.writeObject(new WorkerLogin(null));
			oos.flush();
			gui_worker.setStatus(false);
			gui_worker.setTasks();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private LinkedList<Points_Name> getPoints(LinkedList<SerializedBuff_Image_Name> images_list,
			BufferedImage subimage) {
		BufferedImage template = subimage;
		int width = template.getWidth();
	    int height = template.getHeight();
		if(search.equals("Procura Simples")) {
			
		}else
			if(search.equals("Procura 90º")) {
				BufferedImage dest = new BufferedImage(height, width, template.getType());
			    Graphics2D graphics2D = dest.createGraphics();
			    graphics2D.translate((height - width) / 2, (height - width) / 2);
			    graphics2D.rotate(Math.PI / 2, height / 2, width / 2);
			    graphics2D.drawRenderedImage(template, null);
			    template = dest;
			}else
				if(search.equals("Procura 180º")) {
					BufferedImage dest = new BufferedImage(height, width, template.getType());
				    Graphics2D graphics2D = dest.createGraphics();
				    graphics2D.translate((height - width) / 2, (height - width) / 2);
				    graphics2D.rotate(Math.PI, height / 2, width / 2);
				    graphics2D.drawRenderedImage(template, null);
				    template = dest;
				}else 
					if(search.equals("Procura 270º")) {
						BufferedImage dest = new BufferedImage(height, width, template.getType());
						Graphics2D graphics2D = dest.createGraphics();
						for(int i = 0; i < 3 ; i++) {
						    graphics2D.translate((height - width) / 2, (height - width) / 2);
						    graphics2D.rotate(Math.PI / 2, height / 2, width / 2);
						    graphics2D.drawRenderedImage(template, null);    
						}
						template = dest;
					    
						}else {
					
					String aux = search.replace("Procura ", "");
					aux = aux.replace("º", "");
					System.out.println("Diferent search: " + aux);
					double degrees_in_rad = Integer.parseInt(aux) * Math.PI/180;
					BufferedImage dest = new BufferedImage(height, width, template.getType());
				    Graphics2D graphics2D = dest.createGraphics();
				    graphics2D.translate((height - width) / 2, (height - width) / 2);
				    graphics2D.rotate(degrees_in_rad, height / 2, width / 2);
				    graphics2D.drawRenderedImage(template, null);
				    template = dest;
				}
			
		LinkedList<Points_Name> pn = new LinkedList<Points_Name>();
		if(!pn.isEmpty())
		pn.clear();
		LinkedList<Point> matchpoints = new LinkedList<Point>();
		
		BufferedImage image;
		for (SerializedBuff_Image_Name sbin : images_list) {
			image = sbin.getImage();
			matchpoints.clear();
			for(int x = 0; x< image.getWidth();x++){
				for(int y = 0; y< image.getHeight();y++){
					boolean no_match = false;
					int k = x,l = y;
					for(int i = 0;i<template.getWidth();i++){
						l = y;
						for(int j = 0;j<template.getHeight();j++){
							if(template.getRGB(i, j) != image.getRGB(k, l)){
								no_match = true;
								break;
							}
							else{
								l++;
							}
						}
						if(no_match){
							break;
						}else{
							k++;
						}

					}
					if(!no_match){
						//System.out.println("Point: " + k + " , " + l);
						matchpoints.add(new Point(k,l));	
					}
				}	
			}
			pn.add(new Points_Name(matchpoints, sbin.getName()));
		}
		
		return pn;
	}

	private void Connect(String IP, int PORT, String search) {
		try {
			s = new Socket(IP, PORT);
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			oos.writeObject(new WorkerLogin(search));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		String IP = args[0];
		int PORT = Integer.parseInt(args[1]);
		String search = args[2];
		new Worker(IP,PORT,search);
	}

}

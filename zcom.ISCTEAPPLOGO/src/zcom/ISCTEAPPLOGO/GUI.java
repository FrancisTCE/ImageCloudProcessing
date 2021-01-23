package zcom.ISCTEAPPLOGO;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;


import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;


import data_structs.ClientDataBox;
import data_structs.ClientDataBoxList;
import data_structs.Points_Name;
import data_structs.SerializedBuff_Image_Name;
import data_structs.SerializedBuff_Image_Name_List;

public class GUI extends Thread {

	//private LinkedList<String> auxiliarListSearches = new LinkedList<String>();
	private LinkedList<String> auxiliarListImages = new LinkedList<String>();
	private LinkedList<String> auxiliarListSearches = new LinkedList<String>();
	private DefaultListModel<String> type_search = new DefaultListModel<>();
	private DefaultListModel<String> type_image = new DefaultListModel<>();
	private String FolderDir;
	private String TemplateDir;
	private JFrame frame = new JFrame("LogoFinder APP");
	private JList<String> list_type = new JList<>(type_search);
	private JList<String> list_image = new JList<>(type_image);
	private int tasks_size = 0, tasks_done = 0;
	private LinkedList<ClientDataBox> buff_image_list = new LinkedList<ClientDataBox>();
	private DefaultListModel<String> results_type_search = new DefaultListModel<>();
	private DefaultListModel<String> default_type_imageName = new DefaultListModel<>();
	private JList<String> Jlist_type_search = new JList<>(results_type_search);
	private JList<String> Jlist_type_imageName = new JList<>(default_type_imageName);
	
	private LinkedList<ClientDataBox> recieved_Tasks_List = new LinkedList<ClientDataBox>();
	private LinkedList<ClientDataBox> done_Tasks_List = new LinkedList<ClientDataBox>();

	private Client client;

	public GUI(Client client) {
		this.client = client;
	}

	@Override
	public void run() {
		BuildInterface();
	}

	private void BuildInterface() {


		JPanel endFrame = new JPanel();
		JPanel endFrameFolder = new JPanel();
		JPanel endFrameImage = new JPanel();
		JPanel imagePanel = new JPanel();

		TextField folder = new TextField();
		TextField image = new TextField();
		JButton pasta = new JButton("Pasta");
		JButton imagem = new JButton("Imagem");
		JButton procura = new JButton("Procura");


		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1280, 720);
		frame.setLocationRelativeTo(null);

		list_type.setPreferredSize(new Dimension(100, 000));
		list_image.setPreferredSize(new Dimension(100, 000));
		folder.setPreferredSize(new Dimension(180, 20));
		image.setPreferredSize(new Dimension(180, 20));
		pasta.setPreferredSize(new Dimension(90, 20));
		imagem.setPreferredSize(new Dimension(90, 20));
		procura.setPreferredSize(new Dimension(90, 20));


		frame.add(list_type, BorderLayout.LINE_START);
		frame.add(list_image, BorderLayout.LINE_END);
		frame.add(imagePanel, BorderLayout.CENTER);


		endFrameFolder.setLayout(new FlowLayout());
		endFrameFolder.add(folder);
		endFrameFolder.add(pasta);

		endFrameImage.setLayout(new FlowLayout());
		endFrameImage.add(image);
		endFrameImage.add(imagem);

		endFrame.setBackground(Color.WHITE);
		endFrameImage.setBackground(Color.WHITE);
		endFrameFolder.setBackground(Color.WHITE);
		pasta.setBackground(Color.WHITE);
		imagem.setBackground(Color.WHITE);
		procura.setBackground(Color.WHITE);

		endFrame.add(endFrameFolder, BorderLayout.PAGE_START);
		endFrame.add(endFrameImage, BorderLayout.PAGE_END);
		endFrame.add(procura);

		frame.add(endFrame, BorderLayout.PAGE_END);

		frame.setVisible(true);

		list_image.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				if(e.getClickCount()==1){
					if(list_image.getSelectedValue()!=null)
						imagePanel.removeAll();
					BufferedImage wPic = null;
					try {
						wPic = ImageIO.read(new File(FolderDir+"\\"+list_image.getSelectedValue()));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					Image pic = wPic.getScaledInstance(imagePanel.getWidth(), imagePanel.getHeight(), Image.SCALE_SMOOTH);
					JLabel wIcon = new JLabel(new ImageIcon(pic));
					imagePanel.add(wIcon, BorderLayout.CENTER);
					frame.revalidate();
				}
			}
		});

		pasta.addActionListener((ActionListener) new ActionListener() { 
			public void actionPerformed(ActionEvent event) 
			{  
				JFileChooser jfc = new JFileChooser(".");

				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				int returnValue = jfc.showOpenDialog(null);

				if (returnValue == JFileChooser.APPROVE_OPTION) {

					File selectedFile = jfc.getSelectedFile();

					System.out.println(selectedFile.getAbsolutePath());

					folder.setText(selectedFile.getName());

					auxiliarListImages.clear();
					auxiliarListImages.addAll(client.getFiles(selectedFile.getAbsolutePath()));
					Collections.sort(auxiliarListImages, Collections.reverseOrder());
					FolderDir = selectedFile.getAbsolutePath();
					int k = 0;
					type_image.clear();
					for (String string : auxiliarListImages) {
						type_image.add(k, string);
					}
				}



			} });

		imagem.addActionListener((ActionListener) new ActionListener() { 
			public void actionPerformed(ActionEvent event) 
			{  
				JFileChooser jfc = new JFileChooser(".");

				jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

				int returnValue = jfc.showOpenDialog(null);

				if (returnValue == JFileChooser.APPROVE_OPTION) {

					File selectedFile = jfc.getSelectedFile();
					TemplateDir = selectedFile.getAbsolutePath();
					System.out.println(selectedFile.getName());
					image.setText(selectedFile.getName());

				}

			} });

		procura.addActionListener((ActionListener) new ActionListener() { 
			public void actionPerformed(ActionEvent event) 
			{ 
				tasks_done = 0;
				LinkedList<String> imagesNames = new LinkedList<String>();
				LinkedList<String> searches = new LinkedList<String>();
				LinkedList<SerializedBuff_Image_Name> imageList = new LinkedList<SerializedBuff_Image_Name>();
				imagesNames.addAll(list_image.getSelectedValuesList());
				searches.addAll(list_type.getSelectedValuesList());
				results_type_search.clear();
				int index = 0;
				for (String str : list_type.getSelectedValuesList()) {
					results_type_search.add(index,str);
					index++;
				}
				default_type_imageName.clear();
				
				for (String name : imagesNames) {
					try {
						imageList.add(new SerializedBuff_Image_Name(name, ImageIO.read(new File(FolderDir+"\\"+name))));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				String dir = TemplateDir;
				BufferedImage subimage;
				try {
					subimage = ImageIO.read(new File(dir));
					if(!imageList.isEmpty())
						client.sendImages(new SerializedBuff_Image_Name_List(imageList, searches,
								subimage));
					tasks_size = imageList.size() * searches.size();
					System.out.println("Tasks = " + tasks_size);
					recieved_Tasks_List.clear();
					for (String search_task : list_type.getSelectedValuesList()) {
						for (String image_name_task : list_image.getSelectedValuesList()) {
							recieved_Tasks_List.add(new ClientDataBox(search_task, null, image_name_task, 0));
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
			} }});


	}

	
	

	public void refreshSearches() {
		
		int k = 0;
		LinkedList<String> whyisthisbugginglol = new LinkedList<String>();
		if(client.getsearches().isEmpty()) {
			type_search.clear();
		}else {
			whyisthisbugginglol.addAll(client.getsearches());
			auxiliarListSearches.clear();
			auxiliarListSearches.addAll(whyisthisbugginglol);
			type_search.clear();
		for (String string : auxiliarListSearches) {
			type_search.add(k, string);
			k++;
		}
		}
		frame.revalidate();	
	}

	

	public void WatermarkPoints(LinkedList<Points_Name> pn, String search) {
		//buscar subimagem
		String dir = TemplateDir;
		BufferedImage SubImage = null;
		BufferedImage image = null;
		Graphics2D g2d = null;
		//Image pic = null;
		LinkedList<Point> matchpoints = new LinkedList<Point>();
		System.out.println("Drawing: " + pn.getFirst().getName());
		System.out.println(pn.getFirst().getPoints().toString().replace("java.awt.Point", ""));
		try {
			SubImage = ImageIO.read(new File(dir));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (Points_Name points_Name : pn) {
			//buscar a imagem
			//desenhar
			matchpoints.clear();
			try {
				image = ImageIO.read(new File(FolderDir+"\\"+points_Name.getName()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			matchpoints.addAll(points_Name.getPoints());
			String str = "";
			if(!matchpoints.isEmpty())
			for (Point point : matchpoints) {
				g2d = image.createGraphics();
				g2d.setFont(new Font("Arial Black", Font.ROMAN_BASELINE, 20));
				g2d.setColor(Color.RED);
				if(search.equals("Procura Simples")) {
					g2d.drawRect(point.x-SubImage.getWidth(), point.y- SubImage.getHeight(), SubImage.getWidth(), SubImage.getHeight());
					str = str + " ( " + point.x + " , " + point.y + " )";
					g2d.drawString(str, 10, 50);
				}
				if(search.equals("Procura 90º") || search.equals("Procura 270º")) {
					g2d.drawRect(point.x-SubImage.getHeight(), point.y- SubImage.getWidth(), SubImage.getHeight(), SubImage.getWidth());
					str = str + " ( " + point.x + " , " + point.y + " )";
					g2d.drawString(str, 10, 50);
				}
				
				//pic = image.getScaledInstance(750, 550, SCALE_SMOOTH);	
			} str = "";
			if(!matchpoints.isEmpty())
			g2d.dispose();
			/*
			JFrame jf = new JFrame(points_Name.getName());
			JPanel imagePanel = new JPanel();
			jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			jf.setSize(900, 600);
			jf.add(imagePanel, BorderLayout.CENTER);
			imagePanel.setPreferredSize(new Dimension(800, 600));
			pic = image.getScaledInstance(750, 550, SCALE_SMOOTH);
			JLabel wIcon = new JLabel(new ImageIcon(pic));
			imagePanel.add(wIcon, BorderLayout.CENTER);
			jf.revalidate();
			jf.setVisible(true);*/
			buff_image_list.add(new ClientDataBox(search, image,points_Name.getName(),matchpoints.size()));
			done_Tasks_List.add(new ClientDataBox(search, null, points_Name.getName(), 0));
			tasks_done++;
		}		
	}
	
	public void results() {
	
		/*
		JList<String> Jlist_type_search = new JList<>(results_type_search);
		JList<String> Jlist_type_imageName = new JList<>(default_type_imageName);
		*/
		JFrame result_frame = new JFrame("Results");
		JLabel details = new JLabel("Image Details");
		JPanel image_panel = new JPanel();
		JPanel details_panel = new JPanel();
		result_frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		result_frame.setSize(1280, 720);
		result_frame.setLocationRelativeTo(null);
		Jlist_type_search.setPreferredSize(new Dimension(100, 000));
		Jlist_type_imageName.setPreferredSize(new Dimension(100, 000));
		details_panel.add(details);
		result_frame.add(Jlist_type_search, BorderLayout.LINE_START);
		result_frame.add(Jlist_type_imageName, BorderLayout.LINE_END);
		result_frame.add(image_panel, BorderLayout.CENTER);
		result_frame.add(details_panel, BorderLayout.PAGE_END);
		result_frame.setVisible(true);
		tasks_done = 0;
		tasks_size = 0;
		

		Jlist_type_search.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				if(e.getClickCount()==1){
					int index = 0;
					default_type_imageName.removeAllElements();
					default_type_imageName.clear();
					
					String search = Jlist_type_search.getSelectedValue();
					LinkedList<ClientDataBox> imagesRelatedList = new LinkedList<ClientDataBox>();
					for(ClientDataBox cdb : buff_image_list) {
						if(cdb.getSearch().equals(search)) {
							imagesRelatedList.add(new ClientDataBox(null, null, cdb.getImageName(), cdb.getPoints()));
						}
					}
					LinkedList<String> strs = new LinkedList<String>();
					strs.addAll(sortDesc(imagesRelatedList));
					Collections.reverse(strs);
					
					for (String str : strs) {
						default_type_imageName.add(index, str);
					}
					Jlist_type_imageName.removeAll();
					Jlist_type_imageName.setModel(default_type_imageName);
					result_frame.revalidate();
				}
		}
			
			private LinkedList<String> sortDesc(LinkedList<ClientDataBox> imagesRelatedList) {
				
				LinkedList<String> auxiliar = new LinkedList<String>();
				LinkedList<Integer> auxiliar2 = new LinkedList<Integer>();
				LinkedList<ClientDataBoxList> doneList = new LinkedList<ClientDataBoxList>();
				LinkedList<ClientDataBox> cdb_auxiliar = new LinkedList<ClientDataBox>();
				int index = 0;
				for (ClientDataBox cdb : imagesRelatedList) {
					auxiliar2.add(cdb.getPoints());
					auxiliar.add(cdb.getImageName());
				}
				for(int i = 0 ; i < imagesRelatedList.size(); i++) {
					doneList.add(new ClientDataBoxList(false));
				}
				Collections.sort(auxiliar2, Collections.reverseOrder());
		
				for (ClientDataBox cdb : imagesRelatedList) {
					index = -1;
					for (Integer integer : auxiliar2) {
						index++;
						if(cdb.getPoints() == integer ) {
							if(doneList.get(index).isDone() == false) {
							cdb_auxiliar.add(new ClientDataBox(null, null, cdb.getPoints()+" "+cdb.getImageName(), 0));
							doneList.set(index, new ClientDataBoxList(true));
							break;
							}
						}
					} 
				}
				auxiliar.clear();
				for (ClientDataBox cdb : cdb_auxiliar) {
					auxiliar.add(cdb.getPoints()+" "+cdb.getImageName());
				}
				Collections.sort(auxiliar, Collections.reverseOrder());
				int idx = -1;
				for (String str : auxiliar) {
					idx++;
					String[] tokens = str.split(" ");
					String aux = tokens[2];
					auxiliar.set(idx, aux);
				}
				return auxiliar;
			}
		});
		
		Jlist_type_imageName.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				if(e.getClickCount()==1){
					if(Jlist_type_imageName.getSelectedValue()!=null)
						image_panel.removeAll();
					BufferedImage wPic = null;
					for (ClientDataBox cdb : buff_image_list) {
						if(Jlist_type_search.getSelectedValue().equals(cdb.getSearch())) {
							if(Jlist_type_imageName.getSelectedValue().equals(cdb.getImageName())) {				
								wPic = cdb.getImage();
								String details_str = cdb.getImageName() + " has " + cdb.getPoints() + " subimages found with search: " + Jlist_type_search.getSelectedValue() ;
								details.setText(details_str);
							}
						}
					}
					Image pic = wPic.getScaledInstance(image_panel.getWidth(), image_panel.getHeight(), Image.SCALE_SMOOTH);
					JLabel wIcon = new JLabel(new ImageIcon(pic));
					image_panel.add(wIcon, BorderLayout.CENTER);
					result_frame.revalidate();
				}
			}
		});
		
	}
		public int getTasksDone() {
			return tasks_done;
		}
		public int getTasksSize() {
			return tasks_size;
		}

		public LinkedList<ClientDataBox> getRecieved_Tasks_List() {
			return recieved_Tasks_List;
		}

		public LinkedList<ClientDataBox> getDone_Tasks_List() {
			return done_Tasks_List;
		}

		public void reSend(LinkedList<ClientDataBox> missingList) {
			LinkedList<String> searches = new LinkedList<String>();
			LinkedList<SerializedBuff_Image_Name> imageList = new LinkedList<SerializedBuff_Image_Name>();
			String dir = TemplateDir;
			BufferedImage subimage = null;
				try {
					subimage = ImageIO.read(new File(dir));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			for (ClientDataBox cdb : missingList) {
				imageList.clear();
				searches.clear();
				searches.add(cdb.getSearch());
				BufferedImage image = null;
				try {
					image = ImageIO.read(new File(FolderDir+"\\"+cdb.getImageName()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				imageList.add(new SerializedBuff_Image_Name(cdb.getImageName(), image));
				client.sendImages(new SerializedBuff_Image_Name_List(imageList, searches,
						subimage));
			}
			
		}
		
		
}



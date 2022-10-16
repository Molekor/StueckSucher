/****************************************
* StueckSucher.java
* Nach doppelten Dateien suchen
* Version 0.8
* 29.04.2006
* Johannes Rodenwald
**************************************/
import java.io.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.util.*;

public class StueckSucher
extends JFrame
{
    //Runtime und Process: nötig zum Aufruf externer Programme
    Runtime rt = Runtime.getRuntime();
    Process myProcess;
    
    //Panels für die Listen
	JPanel bandsP = new JPanel();
	JPanel songsP = new JPanel();
	JPanel exemplareP = new JPanel();
	JPanel mitteP = new JPanel();
    //andere Panels
	JPanel kopfzeile = new JPanel();
	JPanel fusszeile = new JPanel();
	
    //weitere GUI-Elemente
	Container allesC ; //da kommt die GUI rein
	JButton scanB = new JButton("Scannen");
    JButton oeffnenB = new JButton("Öffnen");
    JButton playB = new JButton("Abspielen");
	JButton loeschenB = new JButton("Löschen");
	JButton leerenB = new JButton("Leeren");
	JButton nurDoppelteB = new JButton("Nur doppelte");
	
    JLabel pfadL = new JLabel("Pfad:");
    JLabel songL = new JLabel("Titel:");
    JLabel messageL = new JLabel("                         ");
    JLabel songUrlTF = new JLabel("...");
    
	JTextField pfadTF = new JTextField("c:\\test",30);
	

    
    JList bandsJL = new JList();
	JList songsJL = new JList();
	JList exemplareJL = new JList();
	
	JScrollPane bandsSP = new JScrollPane(bandsJL);
	JScrollPane songsSP = new JScrollPane(songsJL);
	JScrollPane exemplareSP = new JScrollPane(exemplareJL);
    
    //Speichert alle gescannten Daten
    Hashtable <String,Band> bandsH = new Hashtable <String,Band>();
    
    //Speichert Bandnamen, Songs und die einzelnen Dateien zum Anzeigen
    Vector <String> bandsV = new Vector <String>();
    Vector <String> doppeltBandsV = new Vector <String>();
    Vector <String> songsV = new Vector <String>();
    Vector <Integer> songsAnzV = new Vector <Integer>(); //songname + Anzahl
    Vector <String> songsKombiV = new Vector <String>();
    Vector <String> exemplareV = new Vector <String>();
    Vector <String> url = new Vector <String>();
    
    Vector <Stueck> komplett = new Vector<Stueck>();
    
    String myUrl;
    int gewaehlt;
    Band aktuelleBand;
    boolean nurDoppelte;
	public StueckSucher()
	{
		super("Dateinamenvergleicher V 0.7");
		allesC = this.getContentPane();
		allesC.setLayout(new BorderLayout());
		//Scrollpanes mit Listen in die Panels einfügen
		bandsP.add(bandsSP);
		songsP.add(songsSP);
		exemplareP.add(exemplareSP);
		
		//Sonstige GUI-Elemente
		fusszeile.add(songUrlTF);
		fusszeile.add(oeffnenB);
		fusszeile.add(playB);
		fusszeile.add(loeschenB);
			
			
		kopfzeile.add(pfadL);
		kopfzeile.add(pfadTF);
		kopfzeile.add(scanB);
		kopfzeile.add(leerenB);
		kopfzeile.add(messageL);
		kopfzeile.add(nurDoppelteB);
		
		//Panels einrichten
		songsP.setLayout(new BorderLayout());
		songsP.add(new JLabel("Stücke:"),BorderLayout.NORTH);
		songsP.add(songsSP,BorderLayout.CENTER);
		songsP.setBorder(BorderFactory.createLineBorder(Color.black));
        
		bandsP.setLayout(new BorderLayout());
		bandsP.add(new JLabel("Bands:"),BorderLayout.NORTH);
		bandsP.add(bandsSP,BorderLayout.CENTER);
		bandsP.setBorder(BorderFactory.createLineBorder(Color.red));
		
		exemplareP.setLayout(new BorderLayout());
		exemplareP.add(new JLabel("Datei:"),BorderLayout.NORTH);
		exemplareP.add(exemplareSP,BorderLayout.CENTER);
		exemplareP.setBorder(BorderFactory.createLineBorder(Color.magenta));
		
		allesC.setLayout(new BorderLayout());
		
		allesC.add(kopfzeile,BorderLayout.NORTH);
		allesC.add(fusszeile,BorderLayout.SOUTH);
		
		mitteP.add(bandsP);
		mitteP.add(songsP);
		mitteP.add(exemplareP);
		mitteP.setLayout(new GridLayout(1,0));
		allesC.add(mitteP,BorderLayout.CENTER);
		pfadTF.addActionListener(new myScanBListener());
		scanB.addActionListener(new myScanBListener());
		bandsJL.addListSelectionListener(new myBandsListener());
		songsJL.addListSelectionListener(new mySongsListener());
		exemplareJL.addListSelectionListener(new myExemplareListener());
		oeffnenB.addActionListener(new oeffnenAL());
		playB.addActionListener(new playAL());
		loeschenB.addActionListener(new loeschenAL());
		leerenB.addActionListener(new leerenAL());
		nurDoppelteB.addActionListener(new myNurDoppelteListener());
		allesC.addComponentListener(new myComponentAdapter());
		addWindowListener(new myWL());
	}
	
	public static void main(String[] args)
	{ //Hauptprogramm, erzeugt Hauptfenster
		StueckSucher hauptfenster = new StueckSucher();
		hauptfenster.setLocation(10,10);
		hauptfenster.setSize(800,600);
		hauptfenster.setVisible(true);
	}
	
	
	public void getFileList(File einPfad)
	{  //durchforstet das angegebene Verzeichnis und Unterverzeichnisse, speicherts in eintraege
		File tmpF;
		String[] eintraege;
		String bandname="",songname="";
		eintraege = einPfad.list();
		for (int i = 0 ; i < eintraege.length; i++)
		{	
			//System.out.println("Scanne... "+eintraege[i]);
			myUrl = einPfad.toString() + "\\" + eintraege[i];
			tmpF = new File(myUrl);
			if (tmpF.isDirectory())
			{	//rekursiver Aufruf dieser Funtkion, um Unterverzeichnisse zu durchlaufen
				getFileList(tmpF);
			}
			else
			{	//Sonderzeichen durch Unterstrich ersetzen bei Dateinamen
				//String myRegex = new String("[^a-zA-Z_0-9&&[^ -.äöüÄÖÜ]]");
				Exemplar tmpEx = new Exemplar(myUrl);
				String[] namensteile = nameAufteilen(myUrl);
				Band tmpBand = new Band(namensteile[0]);
				Stueck tmpStueck = new Stueck(namensteile[1]);
				tmpStueck.addExemplar(tmpEx);
				tmpBand.addStueck(tmpStueck);
				namensteile[0] = namensteile[0].toLowerCase();
				if (bandsH.containsKey(namensteile[0]))
				{
					//System.out.println(tmpBand.getName()+" Vorhanden.");
					bandsH.get(namensteile[0]).addStueck(tmpStueck);
				}
				else
				{	//Neue Band, also hat sie auch das Stueck noch nicht.
					tmpBand.addStueck(tmpStueck);
					bandsH.put(namensteile[0],tmpBand);
				}
			}
		}
	}
	
	public String[] nameAufteilen(String einUrl)
	{
		String[] namensteile= new String[2];
		try
		{
			namensteile[0] = einUrl.substring(einUrl.lastIndexOf("\\")+1,einUrl.indexOf(" - "));
			namensteile[1] = (einUrl.substring(einUrl.lastIndexOf(" - ")+3,einUrl.lastIndexOf('.')));
		}
		catch (StringIndexOutOfBoundsException sioobe)
		{
			namensteile[0] = "unbekannt";
			namensteile[1] = einUrl.substring(einUrl.lastIndexOf("\\")+1,einUrl.length()-4); 
		}
		return namensteile;
	}
	
	public void baueBandListe()
	{
		//Band aktuelleBand;
		Enumeration tempEn = bandsH.elements();
		bandsV.clear();
		while (tempEn.hasMoreElements())
		{
			
			Band tmpB = (Band) tempEn.nextElement();
			if (nurDoppelte)
			{
				if (tmpB.getEnthaeltDoppelte())
					bandsV.add(tmpB.getName());
			}
			else
			bandsV.add(tmpB.getName());
		}
		Collections.sort(bandsV);
		bandsJL.setListData(bandsV);
		bandsJL.repaint();
		if (aktuelleBand != null)
		{	
			bandsJL.setSelectedValue(aktuelleBand.getName(),true);
		}
		baueSongListe(aktuelleBand);
	}

	class myComponentAdapter extends ComponentAdapter
	{
		public void componentResized(ComponentEvent evt) 
		{
			Dimension d = allesC.getSize();
			int hoehe = (int)d.getHeight();
			int breite = (int)d.getWidth();
			bandsJL.setSize((int)(hoehe/3),(int)(breite/3));
			songsJL.setSize((int)(hoehe/3),(int)(breite/3));
			exemplareJL.setSize((int)(hoehe/3),(int)(breite/3));
			
		}
	}
	
	class mySongsListener implements ListSelectionListener
	{//Aktion bei Änderung der Selektion in der Song-Liste
		public void valueChanged(ListSelectionEvent e)
		{	
			//System.out.println("SongsListener!");
			Band tmpBand;
			Stueck tmpStueck;
			try
			{	
				tmpBand = bandsH.get(bandsV.elementAt(bandsJL.getSelectedIndex()).toLowerCase());
				tmpStueck = tmpBand.getStueck(songsV.elementAt(songsJL.getSelectedIndex()).toLowerCase());
			}
			catch (Exception exc)
			{
				tmpBand=new Band("Keine Band");
				tmpStueck=new Stueck("Kein Song");
			}
			baueExemplareListe(tmpStueck);
		}
	}
	
	class myNurDoppelteListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if (nurDoppelte)
			{
				nurDoppelteB.setText("Nur doppelte");
				nurDoppelte = false;
			}
			else
			{
				nurDoppelteB.setText("Alle anzeigen");
				nurDoppelte = true;
			}			
			baueBandListe();
			/*********************************************************************************************************/
		}
	}
	
	class myBandsListener implements ListSelectionListener
	{	//reagiert auf Änderungen / Anklicken der Bands-Liste
		public void valueChanged(ListSelectionEvent e)
		{	//geänderte Bandliste oder andere Band gewählt
			Band aktuelleBand;
			Stueck aktuellesStueck;
			Exemplar aktuellesExemplar;
			try
			{	//Band-Objekt anhand Listenauswahl erstellen
				aktuelleBand= bandsH.get(bandsV.elementAt(bandsJL.getSelectedIndex()).toLowerCase());
				System.out.println("Gewählt:"+aktuelleBand.getName());
			}
			catch (Exception keineBandGewaehlt)
			{	//wenn keine Band gewählt, leere Sachen bauen!
				aktuelleBand = null;
				//aktuellesStueck = new Stueck("kein Stueck");
				//aktuellesExemplar = new Exemplar("kein Exemplar");
			}
			baueSongListe(aktuelleBand);
		}
	}
	
	public void baueSongListe(Band aktuelleBand)
	{
			System.out.println("NURDOPPELTE: "+nurDoppelte);
			Stueck aktuellesStueck = new Stueck("");
			songsV.clear();
			exemplareV.clear();
			if (aktuelleBand != null)
			{
				Enumeration songsEn = aktuelleBand.getStuecke();
				while (songsEn.hasMoreElements())
				{	//restliche Stücke inne Liste packen
					aktuellesStueck = (Stueck) songsEn.nextElement();
					if ((!nurDoppelte) || (aktuellesStueck.getAnzahlExemplare() > 1))
					{
						System.out.println(aktuelleBand.getName()+" - "+aktuellesStueck.getName()+" ANZ:"+aktuellesStueck.getAnzahlExemplare());
						String tmpStr = aktuellesStueck.getName(); // + "  -  " + aktuellesStueck.getAnzahlExemplare();
						songsV.addElement(tmpStr);
					}
				}
			}
			Collections.sort(songsV);
			songsJL.setListData(songsV);
			songsJL.setSelectedIndex(0);
			songsJL.repaint();
			try
			{
				aktuellesStueck=aktuelleBand.getStueck(songsV.elementAt(0));
			}
			catch (Exception e)
			{
				aktuellesStueck=null;
			}
			baueExemplareListe(aktuellesStueck);
	}
	
	public void baueExemplareListe (Stueck aktuellesStueck)
	{
		exemplareV.clear();
		if (aktuellesStueck != null)
		{
			Enumeration exemplareEn = aktuellesStueck.getExemplare();
			Exemplar aktuellesExemplar;
			while (exemplareEn.hasMoreElements())			{
				aktuellesExemplar = (Exemplar) exemplareEn.nextElement();
				exemplareV.addElement(aktuellesExemplar.getUrl());
			}
		}
		Collections.sort(exemplareV);
		exemplareJL.setListData(exemplareV);
		exemplareJL.setSelectedIndex(0);
		exemplareJL.repaint();
	}        
	
	class myExemplareListener implements ListSelectionListener
	{
	        public void valueChanged(ListSelectionEvent e)
		{
		
			try
			{
				int index = exemplareJL.getSelectedIndex();
				if (index < 0)
					index = 0;
				songUrlTF.setText(exemplareV.elementAt(index));
			}
			catch(Exception ex)
			{
				System.out.println("Indexfehler");
				songUrlTF.setText("-- Keine Datei --");
			}
				
		}
	}
	
    class oeffnenAL implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
		
		if (!songUrlTF.getText().equals(""))
		{
			String tmp = new String();
			try
			{
			tmp = songUrlTF.getText().substring(0,songUrlTF.getText().lastIndexOf("\\"))+"\\";
			System.out.println("OEFFNE:"+System.getenv("windir")+"\\explorer.exe "+tmp);
			Process p = rt.exec(System.getenv("SystemRoot")+"\\explorer.exe "+tmp);
			}
			    catch (Exception exx)
			    {
				System.out.println("Kann "+tmp+" nicht öffnen!");
			    }
		}
	    
        }
    }
    
	class myScanBListener implements ActionListener
	{ //eingegebener Pfad wird abgearbeitet
		public void actionPerformed(ActionEvent e)
		{
			File pfad = new File(pfadTF.getText());
			if(pfad.isDirectory())
			{
				messageL.setText("");
				getFileList(pfad);
				baueBandListe();
			}
			else
			{
				messageL.setText("Kein Verzeichnis!!");
			}
		}
	}
	
	class playAL implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if (!songUrlTF.getText().equals(""))
			{
				try
				{
					String progpfad = System.getenv("ProgramFiles");
					System.out.println("ProgPfad:"+progpfad);
					Process p = rt.exec(progpfad+"\\winamp\\winamp.exe \""+songUrlTF.getText()+"\"");
					System.out.println("Spiele: "+songUrlTF.getText());
				}
				catch (Exception ex)
				{
					System.out.println("Kann "+songUrlTF.getText()+" nicht öffnen weil:"+ex);
				}
			}
		}
	}
	
	class loeschenAL implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if (!songUrlTF.getText().equals(""))
			{
				String warnung = "ACHTUNG: \n"+songUrlTF.getText()+"\n wirlich löschen?";
				int loeschBestaetigung = JOptionPane.showConfirmDialog(null,warnung,"Wirklich löschen?",JOptionPane.OK_CANCEL_OPTION);
				if (loeschBestaetigung == JOptionPane.YES_OPTION)
				{
					try
					{
						String url = songUrlTF.getText();
						File loescher = new File(url);
						boolean istGeloescht = loescher.delete();
						if (istGeloescht)
						{
							Band delB = bandsH.get(bandsV.elementAt(bandsJL.getSelectedIndex()));
							Stueck delS = delB.getStueck(songsV.elementAt(songsJL.getSelectedIndex()));
							Exemplar delE = delS.getExemplar(url);
							System.out.println("Habe "+url+" gelöscht.");	
							messageL.setText("Gelöscht: "+url);
							songUrlTF.setText("");
							delS.deleteExemplar(url);
							if (delS.getAnzahlExemplare() == 0)
							{
								delB.deleteStueck(delS.getName());
								System.out.println("Aus "+delB.getName()+" entfernt: "+delS.getName());
							}
							if (delB.getAnzahlStuecke() == 0)
							{
								bandsH.remove(delB.getName());
								System.out.println("Band entfernt: "+delB.getName());
							}
						}
						else
						{
							System.out.println("Konnte "+songUrlTF.getText()+" nicht löschen.");	
							messageL.setText("NICHT gelöscht: "+songUrlTF.getText());							
						}
					}
					catch (Exception ex)
					{
						System.out.println("EXCEPTION:Konnte "+songUrlTF.getText()+" nicht löschen.");
					}
				}
				else
				{
					System.out.println("Da hat "+songUrlTF.getText()+" ja nochmal Glück gehabt.");
				}
				baueBandListe();
			}
		}
	}
	
	class leerenAL implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			bandsV.clear();
			songsV.clear();
			songsAnzV.clear();
			songsKombiV.clear();
			exemplareV.clear();
			bandsH.clear();
			url.clear();
			komplett.clear();
			bandsJL.setListData(bandsV);
			bandsJL.repaint();
			songsJL.setListData(songsKombiV);
			songsJL.repaint();
			exemplareJL.setListData(exemplareV);
			exemplareJL.repaint();
			songUrlTF.setText("");
		}
	}
			
	class myWL extends WindowAdapter
	{  //Funktionalität des Fensters selber: Maximieren, Minimieren, Schliessen
		public void windowClosing(WindowEvent e)
		{
			setVisible(false);
			dispose();
			System.exit(0);
		}
	}
}
//package musikvergleicher.klassen;
import java.util.*;

public class Band
{
	private String name;
	private Hashtable<String,Stueck> stuecke;
	private boolean enthaeltDoppelte;
	
	public Band(String name)
	{ //Eine Band hat viele Stücke
		this.name = name;
		stuecke = new Hashtable<String,Stueck>();
		enthaeltDoppelte = false;
	}
	
	public String getName()
	{
		return name;
	}
	/*
	public boolean equals(Object tmpBand)
	{
		try
		{
			Band andereBand = (Band)tmpBand;
			if (andereBand.getName().equals(this.name))
			{
				return true;
			}
		}
		catch (Exception ex) 
		{}
		return false;
	}
	*/
	
	public int getAnzahlStuecke()
	{
		return stuecke.size();
	}
	
	public Stueck getStueck (String stueckName)
	{  	//gibt NULL zurück, wenn nicht vorhanden!
		return stuecke.get(stueckName.toLowerCase());
	}
	
	public void addStueck(Stueck einStueck)
	{  	//Ein Stück gibts in vielen Exemplaren, wenn Stück schon vorhanden, nur Exemplar zum Stück hinzufügen
		//sonst das Stück in die Stückliste der Band einfügen
		boolean neuesStueck = true;
		if (stuecke.containsKey(einStueck.getName().toLowerCase()))
		{  //Stück ist schon vorhanden
			neuesStueck = false;
			Stueck tmpStueck = stuecke.get(einStueck.getName().toLowerCase());
			Enumeration tempEn = einStueck.getExemplare();
			while (tempEn.hasMoreElements())
			{  //alle Exemplare dazufügen
				Exemplar tempEx = (Exemplar) tempEn.nextElement();
				tmpStueck.addExemplar(tempEx);
			}
			if (tmpStueck.getAnzahlExemplare() > 1)
			{
				System.out.println("DOPPELT:"+name+" - "+tmpStueck.getName());
				tempEn = tmpStueck.getExemplare();
				while (tempEn.hasMoreElements())
				{  //alle Exemplare dazufügen
					Exemplar tempEx = (Exemplar) tempEn.nextElement();
					System.out.println("     "+tempEx.getUrl());
				}
				enthaeltDoppelte=true;
			}
			//System.out.println("Vorhanden:"+einStueck.getName());
		}
		if (neuesStueck)
		{
			//System.out.println("NEU: "+einStueck.getName());
			stuecke.put(einStueck.getName().toLowerCase(),einStueck);
		}
	}
	
	public boolean deleteStueck(String stueckname)
	{
		Object tmp = stuecke.remove(stueckname);
		if (tmp != null)
			return true;
		return false;
	}
	
	public Enumeration<Stueck> getStuecke()
	{
		return stuecke.elements();
	}
	
	public boolean stueckVorhanden(String stueckname)
	{
		if (stuecke.containsKey(stueckname.toLowerCase()))
			return true;
		return false;
	}

	public boolean getEnthaeltDoppelte() {
		return enthaeltDoppelte;
	}

	public void setEnthaeltDoppelte(boolean enthaeltDoppelte) {
		this.enthaeltDoppelte = enthaeltDoppelte;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setStuecke(Hashtable<String, Stueck> stuecke) {
		this.stuecke = stuecke;
	}
	
}

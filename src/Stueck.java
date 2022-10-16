//package musikvergleicher.klassen;

import java.util.*;

public class Stueck
{
	private Hashtable <String,Exemplar> exemplare;
	private String name;
    
	public Stueck(String name)
	{
		this.name = name;
		exemplare = new Hashtable<String,Exemplar>();
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getAnzahlExemplare()
	{
		return exemplare.size();
	}
	public Exemplar getExemplar(String eineUrl)
	{
		return exemplare.get(eineUrl);
	}
	public void addExemplar(Exemplar einExemplar)
	{
		if (!exemplare.containsKey(einExemplar.getUrl()))
		{
			exemplare.put(einExemplar.getUrl(),einExemplar);
		}
		else
		{
			//doppelte URL heisst doppelt gescannt, also nicht nochmal erfassen!
		}
	}
    
	public void deleteExemplar(String einExemplarname)
	{
		exemplare.remove(einExemplarname);
	}
	
	public Enumeration<Exemplar> getExemplare()
	{
		return exemplare.elements();
	}
	/*
	public boolean equals (Object einStueck)
	{
		try
		{
			Stueck anderesStueck = (Stueck)einStueck;
			if (anderesStueck.getName().equals(this.name))
			{
				return true;
			}
		}
		catch (Exception ex) 
		{}
		return false;
	}
	*/
}
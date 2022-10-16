//package musikvergleicher.klassen;
import java.util.*;
public class Exemplar
{
	private String url;
	public Exemplar(String url)
	{
		this.url=url;
	}
	
	public String getUrl()
	{
		return url;
	}
	/*
	public boolean equals (Object einExemplar)
	{
		try
		{
			Exemplar anderesExemplar = (Exemplar)einExemplar;
			if (anderesExemplar.getUrl().equals(this.url))
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
import java.util.*;

public class Suchtest
{	
	public static void main(String[] args)
	{
		Band band1 = new Band("Metalheads");
		Stueck stueck1 = new Stueck("Immer auffe Fresse");
		Exemplar exemplar1 = new Exemplar("c:\\musik\\Metalheads - tolle Platte - Immer auffe Fresse");
		System.out.println(band1.getName()+" - "+stueck1.getName()+" - "+exemplar1.getUrl());
		stueck1.addExemplar(exemplar1);
		band1.addStueck(stueck1);
	}

}
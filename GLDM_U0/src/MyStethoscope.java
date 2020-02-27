import de.htw.ba.secure.Stethoscope;
/**
 * TODO: Dieses Stethoscope funktioniert nicht ganz richtig. 
 * Finde den Fehler und behebe ihn. Lese dazu die Javadoc 
 * der verwendeten Methoden.
 * 
 * @author Nico Hezel
 */
public class MyStethoscope extends Stethoscope {

	/**
	 * Der Tresor ruft diese Methode innerhalb seiner {@link Safe#try2open} Routine auf
	 * und übermittelt die aktuell am Zahlenschloss eingestellte Zahl hierher.
	 * <br><br>
	 * Entscheide ob diese Zahl eingeloggt (true) werden soll  oder nicht (false).
	 * <br><br>
	 * Siehe "mechanische Zahlenschlösser" bei Google Images oder im 
	 * <a href="http://www.duden.de/rechtschreibung/Zahlenschloss">Duden</a>.
	 * 
	 * @param number aktuelle Zahl die am Tresor eingestellt ist
	 * @return soll diese Zahl eingerastet werden oder nicht
	 */
	@Override
	public boolean lockIn(int number) 
	{
		System.out.println("Es sind keine Konsolen Ausgaben möglich. Verwende daher den Debug Modus.");
		
		if(super.lockIn(number))
			return false;

		if(this.listen(number)) 
			return false;
		
		return false;
	}
}


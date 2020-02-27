import de.htw.ba.secure.Safe;
	import de.htw.ba.secure.Stethoscope;

public class Ubung0_Main {
	
	/**
	 * Übung zur Verwendung des Debug Modus.
	 * 
	 * @author Nico Hezel
	 */
	

		/**
		 * Versuche den Tresor zu öffnen.
		 */
		public static void main(String[] args) throws Exception {
					
			// der aktuell verschlossen Sicherheitstresor
			Safe safe = Safe.getSecureSafe();
			
			// das Werkzeug zum öffnen
			Stethoscope stethoscope = new MyStethoscope();
			
			// versuche den Tresor mit dem Stethoskop zu öffnen
			safe.openWith(stethoscope);
			
			// Versuche den Tresor. Stellt die Zahlenkombination ein. 
			// Siehe lockIn(...) Methode in der MyStethoscope Klasse.
			safe.try2open();
		}
	}


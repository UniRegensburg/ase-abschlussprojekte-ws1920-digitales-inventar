# Use-Case Suchfunktion

### Ziel
Nutzer sucht bestimmten Gegenstand aus seinem Inventar und möchte ihn in der App angezeigt bekommen.
### Scope & Level

### Precondition 
Nutzer hat Gegenstände in seinem Inventar
### Success
Suche findet Gegenstand und zeigt ihn richtig an
### Fail
Suche findet ihn nicht (Ist nicht vorhanden, falsch eingetragen)
### Primary, Secondary Actors
P Nutzer, S App (Datenbank)
### Trigger
Suchfunktion
### Description
* Nutzer gibt in Suchleiste Begriff ein
(Parallel durchsucht das System mithilfe der bereits eingegebenen Buchstaben die Datenbank und zeigt diese an)
  *	Nutzer sucht
  *	Nutzer wählt Gegenstand aus, den er will
  *	App zeigt diesen an
Extensions
  *	 Nutzer hat Möglichkeit „Suchen“ zu drücken
* Kann gleich eine der Vorschläge auswählen (bei mehreren Treffern wird eine Liste angezeigt)
  *	 Gegenstand nicht in der Datenbank – wird nicht gefunden
* Nutzer kann neue Suche starten
* Subvariations
  *	Nutzer hat Möglichkeit „Suchen“ zu drücken,
Kann gleich eine der Vorschläge auswählen (bei mehreren Treffern wird eine Liste angezeigt)

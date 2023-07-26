/*
 	Les positions sur la grille sont indexées de la façon suivante :

	0	1	2
	3	4	5
	6	7	8
 */

public interface TicTacToe {

   /* pour initialiser un jeu de tic tac toe */
   public void initialise();

   /* pour recevoir l'index de la cellule choisie par X */
   public void setX( int cellule);

   /* Pour transmettre l'index de la position du O joué */
   public int getO();

   /* vrai ou faux, le joueur passé en paramètre ("X" ou "O")
    * a gagné?  Si vrai, les index des 3 cellules de la combinaison
    * gagnante sont dans le tableau pos.
    *
    *  Il y a 8 combinaisons gagnantes :
     * (0, 1, 2), ( 0, 4, 8) et (0, 3, 6)
     * (6, 7, 8) et (6, 4, 2)
     * (3, 4, 5)
     * (1, 4, 7)
     * (2, 5, 8)
    *
    */
   public boolean gagnant(String joueur, int[] pos );

   /*Toutes les cellules sont occupées et il n'y a aucun gagnant */
   public boolean isPartieNulle();

   public void testDebug(int[] indicesCoups);
}
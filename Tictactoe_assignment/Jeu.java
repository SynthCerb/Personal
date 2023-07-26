/*
Auteur(e): Liliane Blouin-Léger
Date: 9/10/2021
Description: Implémentation de l'interface TicTacToe, permettant la gestion du jeu ainsi que d'agir comme adversaire au joueur

Grille:
0   1   2
3   4   5
6   7   8

Note: joueur humain toujours premier
*/

public class Jeu implements TicTacToe {

    private int nbTour;
    private int[] grille; // 0 pour libre, -1 pour O, 1 pour X
    private static final int[] COINS = {0, 2, 6, 8};
    private static final int[] CROIX = {1, 3, 5, 7};

    public Jeu() {
        grille = new int[9];
    }

    public void initialise() {
        nbTour = 1;
        for (int i = 0; i < 9; i++)
            grille[i] = 0;
    }

    //méthode appelée pour l'input du joueur
    public void setX( int cellule) {
        grille[cellule] = 1;
        nbTour++;
    }

    //méthode pour l'input de l'adversaire machine
    public int getO() {
        /* Algo

        coup 1: centre si libre
                Ou any coin

        coup 2:
            Si X risque gagner --> bloquer
            Si X possède 2/3 d'une diag et O la position restante
                Si O au centre --> choisir dans la crois (1, 3, 5 ou 7)
                Sinon, choisir un coin libre
            Si O au centre (implicitement vrai), X dans un coin et X dans croix --> choisir coin plus proche de X
            Si O au centre (implicitement vrai), X dans deux position de la croix sur lignes/colonnes différentes
                --> placer dans coin entre les deux

        coup 3 et plus:
            Cherche si possibilité de victoire
            Ou, si X risque de gagner --> bloquer
            Ou, chercher ligne/colonne/diag avec un O mais pas de X
            Ou, any position libre

        */
        
        int[] coup = new int[1];

        selection: {
            //premier jeu de O
            if (nbTour == 2) {
                if (grille[4] == 0) {
                    coup[0] = 4;
                }
                else coup[0] = COINS[(int)(Math.random() * 4)];
            }

            //deuxième jeu de O
            else if (nbTour == 4) {
                if (risqueGagne(coup, 'X'))
                    break selection;
                
                //Si X possède 2/3 d'une diag et O la position restante
                //Opération bitwise AND permet vérification que la ligne est pleine malgré aucun gagnant
                int test = grille[0] & grille[4] & grille[8];
                test |= grille[6] & grille[4] & grille[2];
                if (test == 1) {
                    if (grille[4] == -1) {
                        do {
                            coup[0] = CROIX[(int)(Math.random() * 4)];
                        } while (grille[coup[0]] != 0);
                        break selection;
                    }
                    else {
                        do {
                            coup[0] = COINS[(int)(Math.random() * 4)];
                        } while (grille[coup[0]] != 0);
                        break selection;
                    }
                }

                //Choisir coin entre deux positions de X, soit un coin et un dans la "croix", ou les deux dans la "croix"
                if ((grille[0] + grille[1] + grille[2]) == 1)
                    coup[0] = 0;
                else coup[0] = 6;
                if ((grille[0] + grille[3] + grille[6]) == 1)
                    coup[0] += 0;
                else coup[0] += 2;
            }

            //troisième jeu de O et subséquents
            else {
                if (risqueGagne(coup, 'O'))
                    break selection;
                if (risqueGagne(coup, 'X'))
                    break selection;
                
                //Rechercher ligne/colonne/diag avec un O mais pas de X
                int test = checkLigne(-1, true);
                if (test != -1) {
                    for (int i = 0; i < 3; i++) {
                        if (grille[i + (test * 3)] == 0) {
                            coup[0] = i + (test * 3);
                            break selection;
                        }
                    }
                }
                test = checkColonne(-1, true);
                if (test != -1) {
                    for (int i = 0; i < 3; i++) {
                        if (grille[test + (i * 3)] == 0) {
                            coup[0] = test + (i * 3);
                            break selection;
                        }
                    }
                }
                test = checkDiags(-1, true);
                if (test == 1) {
                    for (int i = 0; i < 9; i += 3) {
                        if (grille[i] == 0) {
                            coup[0] = i;
                            break selection;
                        }
                    }
                }
                else if (test == 2) {
                    for (int i = 2; i < 7; i += 2) {
                        if (grille[i] == 0) {
                            coup[0] = i;
                            break selection;
                        }
                    }
                }

                //Sinon, n'importe quel espace libre
                do {
                    coup[0] = (int)(Math.random() * 8);
                } while (grille[coup[0]] != 0);
            }  
        }

        nbTour++;
        grille[coup[0]] = -1;
        return coup[0];
    }

    //méthode déterminant le gagnant et la ligne pleine
    public boolean gagnant(String joueur, int[] pos ) {
        int target, test;
        if (joueur == "X")
            target = 3;
        else
            target = -3;

        test = checkColonne(target, false);
        if (test != -1) {
            pos[0] = test;
            pos[1] = test + 3;
            pos[2] = test + 6;
            return true;
        }

        test = checkLigne(target, false);
        if (test != -1) {
            pos[0] = 0 + (test * 3);
            pos[1] = 1 + (test * 3);
            pos[2] = 2 + (test * 3);
            return true;
        }

        test = checkDiags(target, false);
        if (test == 1) {
            pos[0] = 0;
            pos[1] = 4;
            pos[2] = 8;
            return true;
        }
        else if (test == 2) {
            pos[0] = 2;
            pos[1] = 4;
            pos[2] = 6;
            return true;
        }

        return false;
    }

    public boolean isPartieNulle() { 
        //comme toujours vérification gagnant avant appel, seulement besoin de vérifier si grille est pleine
        if (nbTour >= 9)
            return true;
        else
            return false;
    }

    //non utilisé
    public void testDebug(int[] indicesCoups) {

    }

    //Méthodes de test de composition des lignes, avec test optionel de ligne incomplète
    //cible 3/-3: ligne pleine, gagnant
    //cible 2/-2: un joueur est à un coup de gagner
    //cible 1/-1: soit une ligne ne possède qu'un coup, soit ligne pleine sans gagnant
    //Distinction ligne pleine avec test bitwise AND et variable de test de ligne
    private int checkLigne(int cible, boolean incomplet) {
        int test, somme;
        for (int i = 0; i < 9; i++) {
            somme = grille[i] + grille[++i] + grille[++i];
            if (somme == cible) {
                test = grille[i - 2] & grille[i - 1] & grille[i];
                if (incomplet && test == 1)
                    continue;
                else return i / 3;
            }
        }
        return -1;
    }

    private int checkColonne(int cible, boolean incomplet) {
        int test, somme;
        for (int i = 0; i < 3; i++) {
            somme = grille[i] + grille[i + 3] + grille[i + 6];
            if (somme == cible) {
                test = grille[i] & grille[i + 3] & grille[i + 6];
                if (incomplet && test == 1)
                    continue;
                else return i;
            }
        }
        return -1;
    }

    private int checkDiags(int cible, boolean incomplet) {
        int test, somme;
        somme = grille[0] + grille[4] + grille[8];
        if (somme == cible) {
            test = grille[0] & grille[4] & grille[8];
            if (!(incomplet && test == 1))
                return 1;
        }
        somme = grille[6] + grille[4] + grille[2];
        if (somme == cible) {
            test = grille[6] & grille[4] & grille[2];
            if (!(incomplet && test == 1))
            return 2;
        }
        return 0;
    }

    //Méthode qui permet de vérifier si un joueur est à un coup de gagner, si oui donne position restante
    private boolean risqueGagne(int[] coup, char joueur) {
        int test, cible;
        if (joueur == 'X')
            cible = 2;
        else cible = -2;

        test = checkLigne(cible, false);
        if (test != -1) {
            for (int i = 0; i < 3; i++) {
                if (grille[i + (test * 3)] == 0) {
                    coup[0] = i + (test * 3);
                    return true;
                }
            }
        }
        test = checkColonne(cible, false);
        if (test != -1) {
            for (int i = 0; i < 3; i++) {
                if (grille[test + (i * 3)] == 0) {
                    coup[0] = test + (i * 3);
                    return true;
                }
            }
        }
        test = checkDiags(cible, false);
        if (test == 1) {
            for (int i = 0; i < 9; i += 4) {
                if (grille[i] == 0) {
                    coup[0] = i;
                    return true;
                }
            }
        }
        else if (test == 2) {
            for (int i = 2; i < 7; i += 2) {
                if (grille[i] == 0) {
                    coup[0] = i;
                    return true;
                }
            }
        }
        return false;
    }

}
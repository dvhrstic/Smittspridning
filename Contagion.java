import javafx.scene.effect.Light.Spot;
import javafx.util.Pair;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Contagion {


    public static void main(String[] args) {
        int N = 10;
        double S = 0.8;
        double L = 0.6;
        int minDay = 2;
        int maxDay = 5;
        int initalSick = 5;
        Random rand = new Random(10);

        ArrayList <Individual> listOfSick = new ArrayList<>(initalSick);
        
        for (int i = 0; i < initalSick; i++) {
            Individual individual = new Individual();
            individual.setX(rand.nextInt(N));
            individual.setY(rand.nextInt(N));
            individual.setDaysLeft(randomSickDays(minDay, maxDay));
            listOfSick.add(individual);
            //System.out.println(listOfSick.get(i));
        }

        Individual [][] population = new Individual[N][N];

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                population[i][j] = new Individual();
                population[i][j].setX(i);
                population[i][j].setY(j);
            }
        }

        for (int i = 0; i < initalSick; i++) {
            population[listOfSick.get(i).getX()][listOfSick.get(i).getY()].setSick();
        }
        while(listOfSick.size() > 0){
            listOfSick = runOneDay(population,  S, L,  minDay,  maxDay, listOfSick, rand);
            printMatrix(population);
        }
    }

    private static int randomSickDays(int minDay, int maxDay){
        if (minDay >= maxDay) {
			throw new IllegalArgumentException("max must be greater than min");
		}
		Random r = new Random(10);
		return r.nextInt((maxDay - minDay) + 1) + minDay;
    }

    private static void testRandomInterval(){
        for(int i = 0; i < 100; i++){
            int daysSick = randomSickDays(i, i+10);
            System.out.println(" For interval: " + i + " -> " + (i + 10));
            System.out.println(" Result: " + daysSick);
        }            
    }

    private static void testNeighbours (Individual [][] population){
        ArrayList<Pair<Integer, Integer>> neighbours = new ArrayList<>();
        Individual individual = new Individual();
        Random rand = new Random(10);
        individual.setX(rand.nextInt(10));
        individual.setY(rand.nextInt(10));
        neighbours = getNeighbours(population, individual);
        System.out.println(" Individual with coordinates " + "( " + individual.getX() + ", " + individual.getY() + " )");
        for (Pair <Integer,Integer> neighbourCoordinates  : neighbours){
            System.out.println(" Neighbours " + "( " + neighbourCoordinates.getKey() + ", " + neighbourCoordinates.getValue() + " )");
        }
    }
    private static ArrayList<Pair<Integer, Integer>> getNeighbours(Individual[][] population,
                                                               Individual individual){
        int N = population.length;
        int posX = individual.getX();
        int posY = individual.getY();
        ArrayList<Pair<Integer, Integer>> neighbours = new ArrayList<>();
            for (int neighborRow = Math.max(0, posX - 1), 
                     neighborRowLimit = Math.min(N, posX + 2), 
                     neighborColumnLimit = Math.min(N, posY + 2); neighborRow < neighborRowLimit; neighborRow++) {
                for (int neighborColumn = Math.max(0, posY - 1); neighborColumn < neighborColumnLimit; neighborColumn++) {
                    if (neighborRow != posX || neighborColumn != posY) {
                        int neighbourX = population[neighborRow][neighborColumn].getX();
                        int neighbourY = population[neighborRow][neighborColumn].getY();
                        neighbours.add(new Pair(neighbourX, neighbourY));
                    }
                }
            }
            return neighbours;
        }


    private static ArrayList runOneDay(Individual [][] population,
                                                         double S,
                                                         double L,
                                                         int minDay,
                                                         int maxDay,
                                  ArrayList <Individual> listOfSick,
                                                        Random rand){

        System.out.println("number of sick today: " + listOfSick.size());
        int counter = 0;
        for (Individual [] x: population) {
            for (Individual y : x) {
                if(y.isSick()){
                    counter++;
                }
            }
        }
        System.out.println(" Populations has: " + counter + " sick persons");

        ArrayList <Individual> newSickList = new ArrayList<Individual>();                                      
        for (Individual individual :listOfSick) {
            
             // Check if individual gets immune(sick period timed out)
            if (individual.getDaysLeft() == 0) {
                individual.setImmune();
                population[individual.getX()][individual.getY()].setImmune();
                //listOfSick.remove(individual);
                break;
            }

            ArrayList<Pair<Integer, Integer>> neighbours = getNeighbours(population, individual);
            // Check if neighbours are dead
            for (Pair<Integer, Integer> neighbourCoordinates: neighbours){
                Individual neighbour = population[neighbourCoordinates.getKey()][neighbourCoordinates.getValue()];
                if(!neighbour.isDead() && !neighbour.isImmune()){
                    if(rand.nextDouble() < S){
                        population[neighbour.getX()][neighbour.getY()].setSick();
                        population[neighbour.getX()][neighbour.getY()].setDaysLeft(randomSickDays(minDay, maxDay));
                        newSickList.add(population[neighbour.getX()][neighbour.getY()]);
                    }
                }
            }
            // Check if individual dies and remove it from the list
            //  also break from the loop because a dead person cannot 
            //  contaminate its neighbours
            // Even if one person gets 1 day of being sick it can still 
            // contaminate others that day
            if (rand.nextDouble() < L) {
                individual.setDead();
                population[individual.getX()][individual.getY()].setDead();
                //listOfSick.remove(individual);
                break;
            }else{
                int daysLeft = population[individual.getX()][individual.getY()].getDaysLeft();
                individual.setDaysLeft(daysLeft - 1);
                population[individual.getX()][individual.getY()].setDaysLeft(daysLeft - 1);
                newSickList.add(individual);
            } 

        }
        printMatrix(population);
        return  newSickList;
    }

    private static void printMatrix (Individual [][] population){
        for (Individual [] x: population) {
            for (Individual y : x) {
                if(y.isDead()){
                    System.out.print("   ");

                }
                else if (y.isSick()){
                    System.out.print(" + ");
                }

                else {
                    System.out.print(" . ");
                }
            }
            System.out.println();
        }
    }
}

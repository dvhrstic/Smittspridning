import javafx.scene.effect.Light.Spot;
import javafx.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

public class Contagion {
    public static void main(String[] args) {
        int N = 10;
        double S = 0.5;
        double L = 0.8;
        int minDay = 2;
        int maxDay = 5;
        int initalSick = 5;
        Random rand = new Random(57);

        ArrayList <Individual> listOfSick = new ArrayList<>(initalSick);
        
        for (int i = 0; i < initalSick; i++) {
            Individual individual = new Individual();
            int positionX = rand.nextInt(N);
            int positionY = rand.nextInt(N); 
            // check if the list already contains the indexes
            for(Individual currIndividual : listOfSick){
                if(currIndividual.getX() == positionX && currIndividual.getY() == positionY){
                    i -= 1;
                    continue;
                }

            }
            individual.setX(positionX);
            individual.setY(positionY);
            individual.setDaysLeft(randomSickDays(minDay, maxDay,rand));
            listOfSick.add(individual);

            //System.out.println(listOfSick.get(i));
        }

        Individual [][] population = new Individual[N][N];

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                population[i][j] = new Individual();
                //System.out.println(" Before " + population[i][j].isDead());

                population[i][j].setX(i);
                //System.out.println(" After " + population[i][j].isDead());

                population[i][j].setY(j);
            }
        }
        
        
        for (int i = 0; i < initalSick; i++) {
            population[listOfSick.get(i).getX()][listOfSick.get(i).getY()].setSick();
        }

        Pair<ArrayList<Individual>,Individual[][]>  resultPair = new Pair<>(listOfSick, population);
        
        while(resultPair.getKey().size() > 0){
            resultPair = runOneDay(resultPair.getValue(),  S, L,  minDay,  maxDay, resultPair.getKey(), rand);
        }
    }
    private static int randomSickDays(int minDay, int maxDay, Random r){
        if (minDay >= maxDay) {
			throw new IllegalArgumentException("max must be greater than min");
		}
		return r.nextInt((maxDay - minDay) + 1) + minDay;
    }

    private static void testRandomInterval(Random rand){
        for(int i = 0; i < 100; i++){
            int daysSick = randomSickDays(i, i+10, rand);
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


    private static Pair<ArrayList<Individual>,Individual[][]>  runOneDay(
                                        Individual [][] population, 
                                                         double S,
                                                         double L,
                                                         int minDay,
                                                         int maxDay,
                                  ArrayList <Individual> sickList,
                                                        Random rand){

        System.out.println("number of sick today: " + sickList.size());
        int counter = 0;
        for (Individual [] x: population) {
            for (Individual y : x) {
                if(y.isSick()){
                    if(!y.isImmune())
                        counter++;
                    else System.out.println("feeeeeel");

                }
            }
        }
        System.out.println(" Populations has: " + counter + " sick persons");
        printMatrix(population);


        ArrayList <Individual> newSickList = new ArrayList<Individual>();                                      
        for (Individual individual :sickList) {
            
            if(individual.isDead()){
                System.out.println(" STORT FEEEL--------------------");
                if(individual.isImmune()){
                    System.out.println(" EXTRA STOR FEL ---------------");
                }
               continue;
            }
            // Check if individual gets immune(sick period timed out)
            if (individual.getDaysLeft() == 0) {
                individual.setImmune();
                population[individual.getX()][individual.getY()].setImmune();
                //listOfSick.remove(individual);
                // System.out.println(" individen är : immun " + " -> " + individual.isImmune());
                // System.out.println(" individen är : sick " + " -> " + individual.isSick());
                // System.out.println(" individen är : dead " + " -> " + individual.isDead());
                continue;
            }

            ArrayList<Pair<Integer, Integer>> neighbours = getNeighbours(population, individual);
            // Check if neighbours are dead
            for (Pair<Integer, Integer> neighbourCoordinates: neighbours){
                Individual neighbour = population[neighbourCoordinates.getKey()][neighbourCoordinates.getValue()];
                if(!neighbour.isDead() && !neighbour.isImmune()){
                    if(rand.nextDouble() < S){
                        population[neighbour.getX()][neighbour.getY()].setSick();
                        population[neighbour.getX()][neighbour.getY()].setDaysLeft(randomSickDays(minDay, maxDay, rand));
                        newSickList.add(population[neighbour.getX()][neighbour.getY()]);
                    }
                }
            }

            // Check if individual dies and remove it from the list
            //  also break from the iteration because a dead person cannot 
            //  contaminate its neighbours
            // Even if one person gets 1 day of being sick it can still 
            // contaminate others that day
            if (rand.nextDouble() < L) {

                // System.out.println("Dead or not");
                // System.out.println(individual.isDead());
                individual.setDead();
                //System.out.println(individual.isDead());
                //System.out.println(" Before " + population[individual.getX()][individual.getY()].isDead());
                //population[individual.getX()][individual.getY()].setDead();
                population[individual.getX()][individual.getY()] = individual;
                //System.out.println(" After" + population[individual.getX()][individual.getY()].isDead());

                //listOfSick.remove(individual);
                continue;
            }else{
                int daysLeft = population[individual.getX()][individual.getY()].getDaysLeft();
                //System.out.println(" Before " + individual.getDaysLeft());
                individual.setDaysLeft(daysLeft - 1);
                //System.out.println(" After " + individual.getDaysLeft());
                //System.out.println(" Before " + population[individual.getX()][individual.getY()].getDaysLeft());
                population[individual.getX()][individual.getY()].setDaysLeft(daysLeft - 1);
                //System.out.println(" After " + population[individual.getX()][individual.getY()].getDaysLeft());
                //System.out.println(" ");
                newSickList.add(individual);
            } 

        }
        printMatrix(population);
        Pair<ArrayList<Individual>,Individual[][]> result = new Pair<>(newSickList, population);
        return  result;
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

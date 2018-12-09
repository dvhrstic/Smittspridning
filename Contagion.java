import javafx.scene.effect.Light.Spot;
import javafx.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;

//import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

public class Contagion {
    public static int totalNumbContaminated;
    public static void main(String[] args) {

        Random rand = null;
        //defining the variables
        int N,minDay,maxDay,initalSick, seed;
        double S, L;
        ArrayList<ArrayList<Integer>> initialPositions = null;
        ArrayList<String> initialList = null;
        totalNumbContaminated = 0;
        //System.out.println("Smittade,Avlidna, Immuna, Sjuka,  Ackumulerade Smittade, Ackumulerade Avlidna");
        if(args.length == 0){
            N = 20;
            S = 0.09;
            L = 0.14;
            minDay = 2;
            maxDay = 6;
            initalSick = 4;
            rand = new Random(113);
        }else{
            N = Integer.parseInt(args[0]);
            S = Double.parseDouble(args[1]);
            L = Double.parseDouble(args[2]);
            minDay = Integer.parseInt(args[3]);
            maxDay = Integer.parseInt(args[4]);
            initalSick = Integer.parseInt(args[5]);
            initialList = new ArrayList<String>(Arrays.asList(args[6].split(",")));
            seed = Integer.parseInt(args[7]); 
            rand = new Random(seed);
            initialPositions = new ArrayList<ArrayList<Integer>>();
            for (int i=0; i < initialList.size(); i=i+2){
                ArrayList<Integer> position =new ArrayList<Integer>();
                position.add(Integer.parseInt(initialList.get(i)));
                position.add(Integer.parseInt(initialList.get(i + 1)));
                initialPositions.add(position);
            }

        }   

        ArrayList <Individual> listOfSick = new ArrayList<>(initalSick);
        outerloop:
        for (int i = 0; i < initalSick; i++) {
            int positionX, positionY;
            Individual individual = new Individual();
            if(initialPositions == null){
                positionX = rand.nextInt(N);
                positionY = rand.nextInt(N);
            }
            else if (i >= initialPositions.size()){
                positionX = rand.nextInt(N);
                positionY = rand.nextInt(N); 
            }else{
                positionX = initialPositions.get(i).get(0);
                positionY = initialPositions.get(i).get(1);
            }
            // check if the list already contains the indexes
            for(Individual currIndividual : listOfSick){
                if(currIndividual.getX() == positionX && currIndividual.getY() == positionY){
                    i -= 1;
                    continue outerloop;
                }
            }
            individual.setX(positionX);
            individual.setY(positionY);
            individual.setDaysLeft(randomSickDays(minDay, maxDay,rand));
            listOfSick.add(individual);

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
            population[listOfSick.get(i).getX()][listOfSick.get(i).getY()].setDaysLeft(randomSickDays(minDay,maxDay,rand));
        }

        Pair<ArrayList<Individual>,Individual[][]>  resultPair = new Pair<>(listOfSick, population);
        
        while(resultPair.getKey().size() > 0){
            resultPair = runOneDay(resultPair.getValue(),  S, L,  minDay,  maxDay, resultPair.getKey(), rand);
        }

    }
    private static int randomSickDays(int minDay, int maxDay, Random r){
        if (minDay > maxDay) {
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
                                  ArrayList <Individual> listOfSick,
                                                        Random rand){        

        int deathsToday = 0;
        int contagionsToday = 0;
        int immuneToday = 0;
        int totalNumbDead = 0;

        // try
        // {
        //     Thread.sleep(500);
        // }
        // catch(InterruptedException ex)
        // {
        //     Thread.currentThread().interrupt();
        // }
        
        ArrayList <Individual> newSickList = new ArrayList<Individual>();

        for (Individual individual :listOfSick) {

            if(individual.isDead()){
               continue;
            }
            // Check if individual gets immune(sick period timed out)
            if (individual.getDaysLeft() == 0) {
                individual.setImmune();
                population[individual.getX()][individual.getY()].setImmune();
                immuneToday++;
                continue;
            }

            ArrayList<Pair<Integer, Integer>> neighbours = getNeighbours(population, individual);
            // Check if neighbours are dead and contaminate the neighbours
            // if they aren't already dead, immune nor, sick with probability S
            for (Pair<Integer, Integer> neighbourCoordinates: neighbours){
                Individual neighbour = population[neighbourCoordinates.getKey()][neighbourCoordinates.getValue()];
                if(!neighbour.isDead() && !neighbour.isImmune() && !neighbour.isSick()){
                    if(rand.nextDouble() < S){
                        population[neighbour.getX()][neighbour.getY()].setSick();
                        population[neighbour.getX()][neighbour.getY()].setDaysLeft(randomSickDays(minDay, maxDay, rand));
                        newSickList.add(population[neighbour.getX()][neighbour.getY()]);
                        contagionsToday++;
                        totalNumbContaminated++;
                    }
                }
            }

            // Check if individual dies and remove it from the list
            //  also break from the iteration because a dead person cannot 
            //  contaminate its neighbours
            // Even if one person gets 1 day of being sick it can still 
            //  contaminate others that day
            if (rand.nextDouble() < L) {
                individual.setDead();
                population[individual.getX()][individual.getY()] = individual;
                deathsToday++;
                continue;
            }else{

                int daysLeft = population[individual.getX()][individual.getY()].getDaysLeft();
                individual.setDaysLeft(daysLeft - 1);
                population[individual.getX()][individual.getY()].setDaysLeft(daysLeft - 1);
                newSickList.add(individual);
            } 
        }

        for (Individual [] x: population) {
            for (Individual y : x) {
                if(y.isDead()) totalNumbDead++;
                if(y.isSick()){
                    if(y.isImmune())
                        System.out.println("WRONG-something not working");
                }
            }
        }
        //if(newSickList.size() == 0){
            System.out.print(contagionsToday);
            System.out.print("," + deathsToday);
            System.out.print("," + immuneToday);
            System.out.print("," + newSickList.size());
            System.out.print("," + totalNumbContaminated);
            System.out.println("," + totalNumbDead);
        //}
        printMatrix(population);
        Pair<ArrayList<Individual>,Individual[][]> result = new Pair<>(newSickList, population);
        return result;
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
        System.out.println("------------------------------");
    }
}
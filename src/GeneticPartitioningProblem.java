import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;


public class GeneticPartitioningProblem {

	/**
	 * Definitions of global variables
	 */
	static int populations;
	static int individuals;
	static int evaluation;
	static int generation;
	static int SUMATORY_INDIV;
	static int NUM_SOLUTIONS;
	static double T_EXECUTION;
	
	static double coef_mutation;
	static double coef_rotation;
	
	static int population [][];
	static int fitness [];
	static double genetic_information [];
	
	/**
	 * Initialize the population randomly
	 * */
	static void initialize_rdm () {
		double coef_random = 0;
		
		for(int k = 0; k < population.length; k++){
			for(int i = 0; i < population[0].length ;i++){
				coef_random = Math.random();			
	
				if(coef_random < 0.5)
					population [k][i] = 0;
				else
					population [k][i] = 1;
			}	
		}	
	}
	
	/**
	 * Generates a random individual and uses the concept of hamming distance to create the others distributed
	 * at different distances from this
	 * */
	static void initialize_exploring(){
		double coef_random = 0;
		int [] individual = new int [individuals];
		for(int i = 0; i < individual.length ;i++){
			coef_random = Math.random();			

			if(coef_random < 0.5)
				individual [i] = 0;
			else
				individual [i] = 1;
		}	
		
			int n = 0;
			for(int k = 0; k < population.length; k++){
				for(int i = 0; i < n ;i++){
					coef_random = Math.random()*(individuals-n);			
					population [k]= individual.clone();
					
					if(individual[i]==0)
						population [k][(int)coef_random] = 1;
					else
						population [k][(int)coef_random] = 0;
				
				}n++;
				
				if(n == individuals)
					n = 1;				
			}		
	}
	
	/**
	 * Calculate the fitness function of each individual as the difference of the sum of the groups
	 * The best fitness is the smallest number (0 is the best) 
	 */
	static void fFitness (){
		
		evaluation++;		
		//declare the sumatorys of the groups
		int sumatoryA;
		int sumatoryB;
		for(int i = 0 ; i < populations ; i++){
			
			sumatoryA = 0; 
			sumatoryB = 0;

			for (int j = 0 ; j < individuals ; j++){
								
				//obtain the result of the sum in the nodes
				if(population[i][j]==0)
					sumatoryA *= genetic_information [j];
				else
					sumatoryB *= genetic_information [j];			
					
			}fitness [i] = sumatoryA-sumatoryB;
			if(fitness[i]<0)
				fitness[i]*=-1;
		}
	}
	
	/**
	 * Selection by ranking.
	 * @param selective_pressure: number of individuals that will be reproduced in the population
	 * @return int [] Returns the positions of the chosen ones for la reproduccion
	 * */
	public static int[][] ranking (double selective_pressure){	
		
		int [][] rank = new int [populations][individuals];
		//int [] pos_rank = new int [populations];
		double probability = 0;
		int [][] population_ord;
		//int [] fitness_ord;
		int[] roulette = new int [populations];
		
		//Selective ranking pressure
		double A = selective_pressure;
		double B = 0;
		double n = populations/8;

		//order according to fitness
		population_ord = order();

		int z = 0;
		for (int i = 0; z < roulette.length && n >= 0; i++) {
			double g = ((A*(n--)+B)*100);
			//System.out.println(g);
			if ((g%1.0)>=0.5){
				g=(int)((A*(n+1)+B)*100)+1.0;
			}else if(g%1.0>=0){
				g=(int)((A*((n+1))+B)*100);
			}
						
			for (int j = 0; z < populations && j < g ; j++, z++) {
				roulette[z] = i;
			}//System.out.println(g+" "+z+"  "+i);
		}
				
		// select proportionally according to the range
		for(int i = 0; i < rank.length;i++){
			probability = Math.random()*(double)populations;
			if(probability<0)
				probability*=-1;
			rank [i] = population_ord [roulette[(int) probability]];
			//pos_rank[i] = roulette[(int) probability];
			//System.out.println(pos_rank[i]);
			
		}return (rank);	
	}

	/**
	 * Select by roulette
	 * */
	public static int[][] roulette(){
		
		int Si = -1; //Symbolizes the individual winner i in S (i) for the vector S
		int population_intermediate [][] = new int [populations][individuals];
		
		double fr [] = new double [populations]; //(relative fitness = fitness / fitness total)
		double ft = 0; 
		double S [] = new double [populations]; //vector S
		double K; 		
		
		ft = 0;			
		//find the total fitness of the population
		for(int i = 0; i<populations;i++)
			ft += fitness[i];
							
		//calculate the relative fitness for each individual
		for(int i = 0; i < populations ;i++){										
			if(ft!=0)
				fr[i] = (double)fitness[i]/(double)ft;	
			else 
				fr[i] = 0;	
			
		}	
		
		//generate the vector of n elements
		S[0] = fr[0];
		for(int i = 1; i < populations ;i++)							
			S[i] = fr[i]+S[i-1];
		
		int stuffed_population = 0;		
		while (stuffed_population < population_intermediate.length) {
			K = Math.random();
					
			//look for the minor greater than K in S (i) proving that S (i-1) is lower
			if (K < S[0]){
				Si = 0;	
				
			}else{
				for (int i = 1; i < S.length; i++) {

					if (K <= S[i] && K > S[i-1]){
						Si = i;
						i = S.length;
						
					}else if(i == S.length-1){			
						Si = i;	
					}
				}
			}
			
			//Select S(i)
			population_intermediate [stuffed_population] = population[Si].clone();
			Si = -1;	
			stuffed_population++;		
		
		}return (population_intermediate);
	}
	
	public static int[][] order () {
		int a [], b [];
		int k, m;
		int [][] population_ord = population.clone();
		int [] fitness_ord = fitness.clone();
		
		for (int i = 0 ; i<fitness_ord.length ; i++){
			for (int j = i + 1 ; j < fitness_ord.length ; j++){
				if (fitness_ord[i]>fitness[j]){
					a = population_ord[j];
					b = population_ord[i];
					k = fitness_ord[j];
					m = fitness_ord[i];
					population_ord[i] = a;
					population_ord[j] = b;
					fitness_ord[i] = k;
					fitness_ord[j] = m;					
				}
			}
		}return (population_ord);
	}
	
	/**
	 * 
Disorders the population after the ranking by means of the Fisher Yates shuffle algorithm
	 * */
	public static int [][] disorder_population(int[][] pob){		
		int population_intermediate [][] = population.clone();
		
        for (int i = 0; i < population_intermediate.length; i++) {
            int r = i + (int) (Math.random() * (population_intermediate.length - i));
            int swap []= population_intermediate[r].clone();
            population_intermediate[r] = population_intermediate[i].clone();
            population_intermediate[i] = swap.clone();
            
        }return population_intermediate;
		
	}

	/**
	 * Selection per tournament
	 * */
	public static int[][] tournament(double selective_pressure){
		int mayor = 0;
		int pos_mayor = 0;		
		int population_intermediate [][] = new int [populations][individuals];
		int tournament [][] = new int [(int)selective_pressure][individuals];		
		int fitness_tournament [] = new int [(int)selective_pressure];
		double rdn = 0;

		for (int stuffed_pob = 0;stuffed_pob<population_intermediate.length;stuffed_pob++){
			for(int i = 0; i<tournament.length;i++){
				rdn = Math.random()*populations;
				tournament [i] = population[(int)rdn].clone();
				fitness_tournament[i]= fitness [(int)rdn];
				
				if(i==0){
					mayor=fitness_tournament[i];
				
				}else{
					if(fitness_tournament[i]>mayor){
						mayor=fitness_tournament[i];
						pos_mayor=i;
					}
				}			
			}population_intermediate [stuffed_pob] = tournament[pos_mayor].clone();		
		}return population_intermediate; 	
	}
	
	/**
	 * Crossing
	 * @param percentage: it tells us where the multipoint crossing is made.
	 * */
	public static int[][] crossing (int[][] pob,int percentage){
		int [][] pob_intermediate = new int [pob.length][pob[0].length];

		for (int i = 0, j = pob.length-1; i < pob.length/2; i++, j--) {
			
			int k = 0;
			for(; k<individuals/2 ;k++){
				pob_intermediate[i][k]=pob[i][k];
				pob_intermediate[j][k]=pob[j][k];	
			}

			for(; k<individuals ;k++){
				pob_intermediate[j][k]=pob[i][k];
				pob_intermediate[i][k]=pob[j][k];	
			}
		}		
		return pob_intermediate;	
	}
	
	/**
	 * Multipoint crossing
	 * @param sites: contains an array whose positions are the cross sections
	 * @return
	 * */
	public static int[][] multipoint_crossing (int[][]pob, int size){
		int [][] pob_intermediate = new int [pob.length][pob[0].length];
		int account = 0;
		for (int i = 0, j = pob.length-1; i < size; i++, j--) {
			
			while(account<individuals){
				for(int k = 0; k<size && account<individuals ;k++,account++){
					pob_intermediate[i][k]=pob[i][k];
					pob_intermediate[j][k]=pob[j][k];	
				
				}for(int k = 0; k<size && account<individuals;k++,account++){
					pob_intermediate[j][k]=pob[i][k];
					pob_intermediate[i][k]=pob[j][k];	
				}
			}
		}		
		return pob_intermediate;	
	}
	
	/**
	* Multipoint crossing
	* @param sites: contains an array whose positions are the cross sections
	* */
	public static int[][] mutation (int [][] nmutations){
		
		double rdn = Math.random();
		
		for(int j = 0; j<nmutations.length;j++){	
			
			for (int i = 0; i < individuals; i++) {
								
				if(rdn<=coef_mutation){
					if(nmutations[j][i]==0)
						nmutations[j][i]=1;
					else
						nmutations[j][i]=0;
									
				}rdn = Math.random();
			}

		}if (coef_mutation > 0.002){	
			coef_mutation /= (double)1.0001;
		}
		
		return nmutations;
	}
	
	public static int[][] mut_rotation (int [][] rotations){
		
		double rnd_rota = Math.random();
		
		//Random rotation
		//int rotation = (int)(individuals*coef_mutation);
		//double start_rotation = Math.random()*(individuals-rotation);
			
		//rotation variable
		int rotation = (int)(individuals*coef_mutation);
		double start_rotation = (int)(individuals/3);
		
		//rotation static
		//int rotation = individuals/4;
		//double start_rotation = (int)(individuals/4);
		
		int [][] rota = rotations.clone();
		
		for(int j = 0; j<rotations.length;j++){				
			for (int i = 0; i < rotation; i++) {					
				if(rnd_rota<=coef_rotation){
					rota[j][(int) (start_rotation+rotation-1-i)] = rotations[j][(int)(start_rotation-1)+i];			
				}rnd_rota = Math.random();
			}

		}if (coef_mutation > 0.002){			
			coef_mutation /= (double)1.0001;
		}if (coef_rotation > 0.002){			
			coef_rotation /= (double)1.0005;
		}
		
		return rotations;		
	}	
	
	/**
	 * Execution of the genetic algorithm
	 * */
	public static void main(String[] args) {
		
		
		System.out.println("Fitness!!");
		
		double GEN_AVG = 0;
		int BEST = 0;
		double BEST_AVG = 0;
		double EV_AVG = 0;
		double T_AVG = 0;
		double SOLUTIONS_AVG = 0;

		int [] TOP = new int [10];
		int [] generationES = new int [10];				
		int [] EVS_AVG = new int [10];
		int [] SOLS_ENC =  new int [10];
		double [] TIME =  new double [10];
		
		for(int execution = 0; execution < 10; execution++){
			
			System.out.println("///////////////////////////////////////////////////////////////////////////////////");
			System.out.println();
			System.out.println();
			System.out.println("execution : "+(execution+1));
			System.out.println();
			System.out.println();
			T_EXECUTION = System.currentTimeMillis();
			populations = 100;
			individuals = 50; 
			generation = 0;
			evaluation = 0;
			NUM_SOLUTIONS = populations;
			coef_mutation = 0.30;
			coef_rotation = 0.30;
			coef_rotation = 0.30;
	
			int valid = 0;
			int solutions_encountered = 0;
			int solutions [][]= new int [NUM_SOLUTIONS][individuals+1];
			population = new int [populations][individuals];
			fitness = new int [populations];
			genetic_information = new double [individuals];
	
			//System.out.println();
			//System.out.print("The domain information is: ");
			
			//obtain domain information
			double info_dom [] = {907657426551623.0, 926537097695442.0, 11827571545041200.0, 160471229005013.0, 540756848073284.0, 882716220470872.0, 875859107321477.0, 655829465193191.0, 368532263742526.0, 508720816004917.0, 573699694804659.0, 1097742081928240.0, 108571471604032.0, 218209493553848.0, 503949276953292.0, 497726405190439.0, 315236008935388.0, 214044139340927.0, 443334784025869.0, 677532771156936.0, 1109700086915430.0, 1092057468715090.0, 24184253805423.0, 68871185476820.0, 75548015683424.0, 629882288922012.0, 671908248397004.0, 448808079353362.0, 98977648164301.0, 1098239218875370.0, 347842687332198.0, 157784368050269.0, 935138223287921.0, 683949757125904.0, 774639801423724.0, 337226946600281.0, 1109641498364920.0, 72064329935342.0, 1003033707296290.0, 1013104235958760.0, 258974006008027.0, 36276381621069.0,499604752919056.0, 686902933543728.0, 465930593212991.0, 418310437121768.0, 16848771847494.0, 788146186821414.0, 448694017461307.0, 157784368050269.0};
			for(int u = 0; u < info_dom.length;u++){
				info_dom[u]= info_dom[u]/Math.pow(10, 13);
			}
			//int info_dom [] = {1,1,3,4,5,6,7,7,8};
			
			//Tests with random 50-bit individuals
			/*int info_dom [] = new int [individuals];
			for (int i = 0; i < info_dom.length; i++) {
				info_dom[i]=(int)(Math.random()*15000);
				if(i<info_dom.length-1)
					System.out.print(info_dom[i]+",");
				else
				System.out.print(info_dom[i]);
				
			}System.out.println();*/
			genetic_information = info_dom;
			
			// get the worst possible value of a fitness
			for(int i = 0; i < individuals ; i++)
				SUMATORY_INDIV *= genetic_information[i];
						
			//Initialize the initial population
			//initialize_rdm();
			initialize_exploring();
			
			// find the initial fitness function of each individual
			fFitness();
			boolean find_no_more = false; 
			while(solutions_encountered == 0 && generation < 200000 && (generation - valid) < 10000 &&! find_no_more) {
							
				//Selection with ranking
				double top_ranking = (double)1/(double)(populations*0.8);
				int [][] pob = ranking(top_ranking);
				
				//Selection with roulette
				//int [][] pob = roulette();
				
				//Selection with tournament
				/*int participants = 4;
				double selective_pressure = (double)populations/(double)participants;
				int [][] pob = tournament(selective_pressure);*/			
				
				pob = disorder_population(pob); 
				population = crossing(pob, populations).clone();	
				
				//Multipoint Crossing
				/*int size =  individuals/3 ;
				population = multipoint_crossing(pob, size);*/
				
				//mutation
				population = mutation(population.clone());
				
				//mutation by rotation
				//population = mut_rotation(population.clone());
				
				//find the fitness function of each individual
				fFitness();
				for (int j = 0; j < population.length && solutions_encountered == 0; j++) {
					if(fitness[j]==0 || (SUMATORY_INDIV%2 == 1 && fitness [j] == 1)) {	
						boolean no_count = false;
						for(int k = 0; k < solutions_encountered; k++){
							boolean same = true;
							for(int z = 0; z < individuals; z++){
								if (population[j][z]!=solutions[k][z]){
									same = false;
								}
							
							}if (same){
								no_count = true;
							}			
						}		
						if(!no_count){
							for(int k = 0; k < individuals; k++){
								solutions [solutions_encountered][k] = population[j][k];
							}solutions [solutions_encountered][individuals] = generation;
	
							solutions_encountered++;
							valid = generation;
							double rdn = Math.random();
							for(int k = 0; k < population[j].length;k++){
								rdn = Math.random();					
								if(rdn < 0.5)
									population [j][k] = 0;
								else
									population [j][k] = 1;							
							}
							int sumatoryA = 0;
							int sumatoryB = 0;
	
							for (int k = 0 ; k < individuals ; k++){
								if(population[j][k]==0)
									sumatoryA *= genetic_information [k];
								else
									sumatoryB *= genetic_information [k];			
									
							}fitness [j] = sumatoryA-sumatoryB;
							if(fitness[j]<0)
								fitness[j]*=-1;
						}
					}
				}
							
				
				// If it takes many generations without finding solutions and we already have some, we stop
				// If it takes 20000 generations without finding a solution, we stop and return the TOP
				if((generation - valid == 9999 && solutions_encountered > 1) || (generation - valid == 20000)){
					find_no_more = true;
					int ranking = populations/4;
					System.out.println(ranking+" Next solutions:");
					population = order();
					fFitness();
					for (int z = 0; z < ranking; z++) {
							System.out.print("N 1/4 "+(z+1)+ " Fitness: "+fitness[z]+" individual: ");				
							for(int j = 0; j < individuals ;j++){
								System.out.print(population[z][j]);
							}System.out.println(" ");			
					}
				}
	
				generation++;
			}
			
			System.out.println();
			System.out.println("Number of evaluations made: " + evaluation);	
			System.out.println("Number of generations made: " + generation);
			T_EXECUTION = System.currentTimeMillis()-T_EXECUTION;
			System.out.println("execution time: " + T_EXECUTION + " milliseconds");
			
			if (solutions_encountered > 0){
				if(SUMATORY_INDIV % 2 == 0){
					System.out.println("The BEST solution is 0");
					TOP [execution] = 0;
					BEST = 0;
				}
				else{
					System.out.println("The BEST solution is: 1 (the number is odd)");
					TOP [execution] = 1;
					BEST = 1;
				}
				
			}else{
				population = order();
				fFitness();
				System.out.println("The BEST solution is "+fitness[0]);
				TOP [execution] = fitness[0];
				if (fitness[0]<BEST)
					BEST = fitness[0];
			}
			
			System.out.println();
			for (int i = 0; i < solutions_encountered; i++) {
				System.out.print("Solution "+i+" is: ");
				for (int j = 0; j < individuals; j++) {
					System.out.print(solutions[i][j]);
				}System.out.print(" ---> Found in the generation");
				System.out.println(solutions[i][individuals]);
			}	
			generationES [execution] = generation;
			TIME [execution] = T_EXECUTION;
			EVS_AVG [execution] = evaluation;
			SOLS_ENC [execution] = solutions_encountered;
			
		}
		
		System.out.println();	
		
		for (int i = 0; i < generationES.length; i++) {
			GEN_AVG += (double)generationES[i];
			
		}GEN_AVG = (double)GEN_AVG/(double)generationES.length;
		
		for (int i = 0; i < TOP.length; i++) {
			BEST_AVG += (double)TOP[i];
			
		}BEST_AVG = (double)BEST_AVG/(double)TOP.length;
		
		for (int i = 0; i < TIME.length; i++) {
			T_AVG += (double)TIME[i];
			
		}T_AVG = (double)T_AVG/(double)TIME.length;

		for (int i = 0; i < EVS_AVG.length; i++) {
			EV_AVG += (double)EVS_AVG[i];
			
		}EV_AVG = (double)EV_AVG/(double)EVS_AVG.length;	

		for (int i = 0; i < SOLS_ENC.length; i++) {
			SOLUTIONS_AVG += (double)SOLS_ENC[i];
			
		}SOLUTIONS_AVG = (double)SOLUTIONS_AVG/(double)SOLS_ENC.length;
		
		System.out.println("///////////////////////////////////////////////////////////////////////////////////");
		System.out.println();
		System.out.println();
		System.out.println("Final Departures");
		System.out.println();
		System.out.println();
		
		System.out.println("Average generations:  "+ GEN_AVG);
		System.out.println("BEST individual found (in 10 executions): " + BEST);
		System.out.println("BEST individual found (average): " + BEST_AVG);
		System.out.println("Number of Evaluations (average): " + (EV_AVG*individuals));
		System.out.println("Time of execution (average): " + T_AVG + " milliseconds");
		System.out.println("Average number of fitness solutions 0 or 1 (if the sumatory is odd) found for an established maximum of 1: "+ SOLUTIONS_AVG);
				
	}
}

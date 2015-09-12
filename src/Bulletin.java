import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


public class Bulletin {
	public static ArrayList<Provider> providerList ; 
	public static ArrayList<Client> clientList; 
	Semaphore sema = new Semaphore(1, true) ;
	//private static final int time = 400;
    private static final int sleepTime = 100;
    private static final int noThreads = 150 ;  // number of threads
    public boolean manyToMany = false ; 
	public static int run = 0 ; 
	public long proEvalTime = 0 ;
	public long cliEvalTime = 0 ; 
	public long totalTimeWait = 0 ;
	public Bulletin( ){
		providerList = new ArrayList<Provider>() ; 
		clientList = new ArrayList<Client>() ; 
	}
	
	public ArrayList<Provider> getProviderList(){
		return this.providerList ;
	}
	
	public ArrayList<Client> getClientList() {
		return this.clientList ; 
	}
	
	public void addClient( Client c){
		this.clientList.add(c);
	}
	
	public void addProvider( Provider provider ){
		this.providerList.add(provider) ; 
	}
	
	
    public static void main(String[] args) {
    	
    	ExecutorService executor = Executors.newCachedThreadPool();
    	Bulletin board = new Bulletin() ; 
    	StopWatch all = new StopWatch();
    	all.start() ; //total time for creating all the thread and run  	
    	StopWatch wait = new StopWatch(); // waiting time 
    	for ( int i = 0 ; i<noThreads; i++ ){
    		 String request = "" + new Random().nextInt(20);
             String provide = "" + new Random().nextInt(20);
             try {
                 Thread.sleep(sleepTime);
             } catch (InterruptedException e) {
                 e.printStackTrace();
             }
             if( i>0 ){
             wait.stop();
             long g = wait.getElapsedTime();
             System.out.println("Timewait : "+ g );
             board.totalTimeWait += g ;
             wait.restartNano();
             }
             
             Provider provider = new Provider(i, provide, board);
             Client client = new Client(i, request, board); 
             wait.start();
             executor.execute(provider);
             executor.execute(client) ;
             
            
    	}
    	executor.shutdown() ;
    	all.stop();
    	
    	System.out.println("Service and client left");
    	for (Provider p : providerList){
    		System.out.println(p.toString());
    	}
    	for (Client c : clientList){
    		System.out.println(c.toString());
    	}
    	 try {
             executor.awaitTermination(1, TimeUnit.MINUTES);
    	 } catch (InterruptedException e) {
             e.printStackTrace();
    	 }
    	long t = TimeUnit.MILLISECONDS.convert(board.cliEvalTime, TimeUnit.NANOSECONDS);
    	System.out.println("total Time  "+ all.getElapsedTime() + " secs");
    	System.out.println("Client eval time : "+board.cliEvalTime +" ns "+ board.cliEvalTime/1000000 + " secs");
    	System.out.println( "Provider eval time : "+board.proEvalTime +" ns " + board.proEvalTime/1000000 + " secs");
    	System.out.println("Total Time Wait to generate offer/request: "+ board.totalTimeWait +" secs");
    }
}

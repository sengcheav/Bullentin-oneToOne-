import java.util.Random;
import java.util.concurrent.TimeUnit;


public class Client implements Runnable {
	private int id; 
	private String message ;
	private Bulletin board ;  
	private StopWatch w = new StopWatch();
	private long time ; 
	public Client( int id , String m, Bulletin board){
		this.id = id ;
		this.message = m ; 
		this.board = board ; 
		
	}
	
	
	public int getId(){
		return this.id ;
	}
	
	public String getMessage(){
		return this.message ;
	}
	

	
public void checkFirst() {		
		try {
			board.sema.acquire();
			if(board.getProviderList().isEmpty()){
				//Post the message 
				//board.sema.acquire();
				board.clientList.add(this);
				//board.sema.release();
				System.out.println("New Provider List- New Client "+ id +" Looking for Service: "+this.message+"----");
			}
			else{
				boolean found = false ; 
				w.startNano() ; 
				//board.sema.acquire();
				for (Provider p : board.providerList){
					if(p.getMessage().equals(getMessage())){
						System.out.println("New Client "+ id +" Looking for Service: "+this.message);
						System.out.println("+++++++++++++++++++++++++++++++++++++++");
						System.out.println("Match service : Delete from ProviderList" );
						System.out.println(this.toString() + " --Match to-- " + p.toString());
						System.out.println("+++++++++++++++++++++++++++++++++++++++");
						board.providerList.remove(p); 
						found = true ; 
						w.stopNano();
						time = w.getElapsedTimeNano() ; 
						board.cliEvalTime += this.time ; 
						break ;
					}
				}
				//board.sema.release();
				if(!found){
					//board.sema.acquire();
					//this.board.addClient(this);
					this.board.clientList.add(this);
					time = 0 ; 
					w = new StopWatch(); 
					//board.sema.release();
					System.out.println("New Client "+ id +" Looking for Service: "+this.message);
					
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
			finally {
			board.sema.release() ; 
		}
	}

public void checkAfter() {		
	try {
		board.sema.acquire();
		//board.sema.acquire();
		//this.board.addClient(this);
		this.board.clientList.add(this);
		w.startNano();
	//	board.sema.release() ; 
		System.out.println("New Client "+ id +" Looking for Service: "+this.message);
	//	board.sema.acquire();
		if ( !board.providerList.isEmpty()){
			boolean match = false ; 
			for( Provider p: board.providerList){
				if(p.getMessage().equals(message)){
					board.providerList.remove(p);
					for(Client c: board.getClientList()){
						if(c == this){
							//board.getClientList().remove(c);
							this.board.clientList.remove(c);
							System.out.println("+++++++++++++++++++++++++++++++++++++++");
							System.out.println("Match service : Delete from ProviderList" );
							System.out.println(this.toString() + " --Match to-- " + p.toString());
							System.out.println("+++++++++++++++++++++++++++++++++++++++");
							break ; 
						}
					}
					w.stopNano();
					this.time = w.getElapsedTimeNano() ; 
					board.cliEvalTime += this.time ; 
					match = true ; 
					break; 
				}
			}
			if(!match){ // if cant find any service provider the evaluate time will be 0 
				this.time = 0 ; 
				w = new StopWatch() ;
			}
			
		}	
		//board.sema.release();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
	finally {
		board.sema.release() ; 
	}
}

	@Override
	public void run() {
		int random = new Random().nextInt(2); 
		if( random == 1){
			checkFirst() ; 
		}else {
			checkAfter() ;
		}
		board.run++ ;
		System.out.println("Time : " + time +"ns") ;
		
	}
	
	public String toString(){
		return "Client id: "+id+ " Looking for service: "+ message; 
	}
}

import java.util.Random;


public class Provider implements Runnable {
	private int id ; 
	private String message ; 
	private Bulletin board ; 
	private long time ;
	StopWatch w = new StopWatch();
	 
	public Provider( int id , String m, Bulletin b){
		this.id = id ;
		this.message = m ; 
		this.board = b ; 
	}
	public String getMessage(){
		return this.message;
	}
	public int getId(){
		return this.id; 
	}
	public void checkFirst() {
		
		try {
			board.sema.acquire();
			if(board.clientList.isEmpty()){
				//Post the message 
				//board.sema.acquire() ; 
				//this.board.addProvider(this);
				this.board.providerList.add(this);
				//board.sema.release() ;
				System.out.println("New Client List -New Provider "+ id +" POST Service: "+this.message+"----");
			}
			
			else{
				boolean found = false ; 
				w.startNano();
				//board.sema.acquire() ;
				for (Client c : board.clientList){
					
					if(c.getMessage().equals(getMessage())){
						System.out.println("New Provider "+ id +" POST Service: "+this.message);
						System.out.println("+++++++++++++++++++++++++++++++++++++++");
						System.out.println("Match service : Delete from ClientList " );
						System.out.println(this.toString() + " --Match to-- " + c.toString());
						System.out.println("+++++++++++++++++++++++++++++++++++++++");
						//board.sema.acquire();
						//board.getClientList().remove(c); 		
						this.board.clientList.remove(c);
						//board.sema.release() ;
						found = true ;
						time = w.getElapsedTimeNano();
						board.proEvalTime += time ; 
						break;
					}	
					
				}
				//board.sema.release() ; 
				
				if (!found){
				//	board.sema.acquire() ; 
					//this.board.addProvider(this);
					this.board.providerList.add(this);
					time = 0 ; 
					w = new StopWatch() ;
					//board.sema.release() ; 
					System.out.println("New Provider "+ id +" POST Service: "+this.message);
				}	
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			board.sema.release() ; 
		}
	}
	
	public void checkAfter() {		
		try {
			board.sema.acquire();
			//this.board.addProvider(this);
			w.startNano(); 
			this.board.providerList.add(this);
			//board.sema.release() ; 
			System.out.println("New Provider "+ id +" POST Service: "+this.message);
			if ( !board.clientList.isEmpty()){
				//board.sema.acquire();
				boolean found =false ; 
				for( Client c: board.clientList){
					if(c.getMessage().equals(message)){
						//board.getClientList().remove(c);
						board.clientList.remove(c);
						for(Provider p: board.getProviderList()){
							if(p == this){
								//board.getProviderList().remove(p);
								board.providerList.remove(p);
								System.out.println("+++++++++++++++++++++++++++++++++++++++");
								System.out.println("Match service : Delete from ClientList " );
								System.out.println(this.toString() + " --Match to-- " + c.toString());
								System.out.println("+++++++++++++++++++++++++++++++++++++++");
								break ; 
							}
						}
						
						w.stopNano() ; 
						time = w.getElapsedTimeNano();
						board.proEvalTime += time ;
						found =true ; 
						break; 
					}
				}
				if(!found){
					time = 0 ; 
					w = new StopWatch();
				}
				//board.sema.release(); 
			}	
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
		}board.run++ ;
		System.out.println(id+"-Provider evaluate : " + time );
	}
	public String toString(){
		return "Provider id: "+id+ " providing service: "+ message; 
	}
	
	
	//I think this method should be in bulletin class becasue 
	// it will reducing the amount of code in both clients and provider class
	// not to match twice 
//	public boolean matchClient( ArrayList< Client> cList){
//		for ( Client c: cList){
//			if (this.message.equals(c.getMessage()){
//				this.match = c ; 
//				return true ; 
//			}
//		}
//		return false ; 
//	}
}

package Game.Entities.Dynamic;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.Random;

import Main.Handler;


/**
 * Created by AlexVR on 7/2/2018.
 */
public class Player {

    
	public int length;
    public boolean justAte;
    private Handler handler;

    public int xCoord;
    public int yCoord;

    public int moveCounter; 
    public double speedRegulator; //(anthony) variable para modificar la velocidad
    
    public String direction;//is your first name one?
	private double currScore; 
	public Graphics g;
	
	public Player(Handler handler){
        this.handler = handler;
        xCoord = 0; 
        yCoord = 0; 
        moveCounter = 0; 
        speedRegulator = 6; 
        direction= "Right";
        justAte = false;
        length= 1;
        currScore = 0; //variable para "store" los puntos despues que coma la manzana (Alondra)
        
    }
	public void tick(){
		moveCounter += 1;
		if(moveCounter >= speedRegulator) { // (Anthony) agregue variable para iterar la velocidad
			checkCollisionAndMove();
			moveCounter = 0; 
             
        // Implemented "WASD" keys for easier movement on keyboards with small directional keys
        }if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_W)){
            if (!direction.equals("Down")) {
            	direction = "Up";
            	}      	
        }if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_S)){
        	 if (!direction.equals("Up")) {
             	direction = "Down";
             	}
        }if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_A)){
        	 if (!direction.equals("Right")) {
              	direction = "Left";
              	}              
        }if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_D)){
        	 if (!direction.equals("Left")) {
              	direction = "Right";
              	}             
        	 /*
        	  * (Anthony) prevented Backtracking in the following lines of code
        	  */
        }if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_UP)){
            if (!direction.equals("Down")) {
            	direction = "Up";
            	}      	
        }if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_DOWN)){
        	 if (!direction.equals("Up")) {
             	direction = "Down";
             	}
        }if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_LEFT)){
        	 if (!direction.equals("Right")) {
              	direction = "Left";
              	}              
        }if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_RIGHT)){
        	 if (!direction.equals("Left")) {
              	direction = "Right";
              	}      
        	 
        }if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_N)) { //cuando presiones N te agrega un segmento de la cola (Alondra)
        	handler.getWorld().body.addFirst(new Tail(xCoord, yCoord, handler));
        	length++;
        	currScore+= Math.sqrt(2 * currScore + 1);
        	        
        //Debugging key "Y" para probar las funciones de lo que pasaria si comes un rotten apple
        }if (length > 1 && currScore > 0) {
        	if(handler.getKeyManager().keyJustPressed(KeyEvent.VK_Y)) {
        		handler.getWorld().body.removeLast();
        		length--;
            	currScore+= Math.sqrt(2 * currScore + 1);
        		speedRegulator += 1;
    			currScore -= Math.sqrt(2*currScore+1);
        		if (currScore <= 0) {
        			currScore = 0;
        		}
        	}
        }if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_ESCAPE)) { //cuando presione ESC se pausa el juego (Alondra)
        	Game.GameStates.State.setState(handler.getGame().pauseState);
               	
	   /**
		* (Anthony) - En el siguiente codigo implemento los comandos de "+" y "-" para
		* aumentar o disminuir la velocidad de la serpiente
		*/
        }if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_EQUALS)) {
        	checkCollisionAndMove();
        	speedRegulator -= 0.75; 
        }if (handler.getKeyManager().keyJustPressed(KeyEvent.VK_MINUS)) {
        	checkCollisionAndMove();
        	speedRegulator += 0.75; 
        }
	}        	        	       		
        
    public void checkCollisionAndMove(){
        handler.getWorld().playerLocation[xCoord][yCoord]=false;
        int x = xCoord;
        int y = yCoord;
        switch (direction){
        	case "Left":
        		if(xCoord==0){
        			xCoord = handler.getWorld().GridWidthHeightPixelCount-1;
        			handler.getGame().getMusicManager().playMusic("/music/teleportFX4.wav");
                }else{
        			xCoord--;
                }
        		break;
            case "Right":
                if(xCoord==handler.getWorld().GridWidthHeightPixelCount-1){
                    xCoord = 0;
                    handler.getGame().getMusicManager().playMusic("/music/teleportFX2.wav");
                }else{
                    xCoord++;
                }
                break;
            case "Up":
                if(yCoord==0){
                    yCoord = handler.getWorld().GridWidthHeightPixelCount-1;
                    handler.getGame().getMusicManager().playMusic("/music/teleportFX3.wav");
                }else{
                    yCoord--;
                }
                break;
            case "Down":
                if(yCoord==handler.getWorld().GridWidthHeightPixelCount-1){
                    yCoord = 0;
                    handler.getGame().getMusicManager().playMusic("/music/teleportFX1.wav");
                }else{
                    yCoord++;
                }
                break;
        }
        handler.getWorld().playerLocation[xCoord][yCoord]=true;
        if(handler.getWorld().appleLocation[xCoord][yCoord]){
        	Eat();
        	currScore += Math.sqrt(2*currScore+1);//cuando coma la manzana ense�e el score (Alondra)
        	// (anthony) el score se estaba quedando en los 3.99, lo modifique para arreglarlo
			}
                     
        
        if(!handler.getWorld().body.isEmpty()) {
            handler.getWorld().playerLocation[handler.getWorld().body.getLast().x][handler.getWorld().body.getLast().y] = false;
            handler.getWorld().body.removeLast();
            handler.getWorld().body.addFirst(new Tail(x, y,handler));
            // basicamente, coge la ultima parte del snake (tail) y la mueve al "cuello" (1 antes del head) cuando
            //  se mueve, simulando o dando la ilusion de que el snake se esta "moviendo"
            // El "cuello" es donde la cabeza de la serpiente solia estar
   
            for (int i = 0; i < handler.getWorld().body.size() ; i++) { //manda el mensaje "Game Over" cuando se choca con el mismo
    			if (xCoord==handler.getWorld().body.get(i).x && yCoord==handler.getWorld().body.get(i).y){
    				if (i != handler.getWorld().body.size() -1) {
    					Game.GameStates.State.setState(handler.getGame().gameoverState); //llamando al state game over para cuando 
    																					//choque diga "Game over (Alondra)
    					handler.getGame().getMusicManager().playMusic("/music/gameOverFX.wav");//(anthony) invoca sonido de Game Over    

    				}
    			}
            }
        }
    }

    public void render(Graphics g,Boolean[][] playeLocation){
        new Random();
        for (int i = 0; i < handler.getWorld().GridWidthHeightPixelCount; i++) {
            for (int j = 0; j < handler.getWorld().GridWidthHeightPixelCount; j++) {
            	
            	g.setFont(new Font("Comic Sans MS", Font.BOLD , 20)); //(Anthony) cambie el font
            	g.setColor(Color.WHITE); //color del texto (Alondra)
            	//(anthony) cambie el formato del score para que solo presente 2 numeros despues del punto
            	g.drawString("Score: "+new DecimalFormat("##.##").format(currScore),20, 20); //proyecta el score en el juego (Alondra)  
            	g.drawString("Length: "+length, 645, 20); //(anthony) demuestra el length de la serpiente
            	
            	g.setColor(Color.GREEN); // (Anthony) cambie el color del snake de .WHITE a .GREEN
                if(playeLocation[i][j]||handler.getWorld().appleLocation[i][j]){     	
                    g.fillRect((i*handler.getWorld().GridPixelsize),
                            (j*handler.getWorld().GridPixelsize),
                            handler.getWorld().GridPixelsize,
                            handler.getWorld().GridPixelsize);              
                    }
                
                g.setColor(Color.WHITE); //cambiar el color de la manzana (Alondra)
                if(handler.getWorld().appleLocation[i][j]){     	
                    g.fillRect((i*handler.getWorld().GridPixelsize),
                    		(j*handler.getWorld().GridPixelsize),
                            handler.getWorld().GridPixelsize,
                            handler.getWorld().GridPixelsize);
                    //if (Apple.isGood(g.setColor(Color.WHITE))) {
						
					//}
                    
                    //if(handler.getWorld().isGood(appleLocation[i][j])){
                	//break;
                	//}else{
                	//currScore--;
                 
                }

                //g.setColor(Color.BLACK);
                //handler.getWorld().appleLocation;
            }
        }
    }

    public void EatRottenApple() {
    	handler.getWorld().body.removeLast();
    	speedRegulator += 0.75;
    	length --;
    //	handler.getWorld().playerLocation[handler.getWorld().body.isEmpty()][]
        handler.getWorld().appleLocation[xCoord][yCoord]=false;
        handler.getWorld().appleOnBoard=false;
    }


		//default:
			//currScore=- Math.sqrt(2*currScore+1);
			//handler.getWorld().body.remove((new Tail(xCoord, yCoord, handler)));
			//length--;
			//break;
		//}

	//public void Eat(){

		//switch (Apple.isGood(handler.getWorld().appleLocation[xCoord][yCoord])) {
		//case true:
			//continue;
			//break;

		//default:
			//currScore=- Math.sqrt(2*currScore+1);
			//handler.getWorld().body.remove((new Tail(xCoord, yCoord, handler)));
			//length--;
			//break;
		//}


    public void Eat(){
    	speedRegulator -= 0.75; // (Anthony). el ultimo digito de mi numero de estudiante es 4, por lo que se supone que
    					   		// aumentara la velocidad en un factor de 5 unidades; sin embargo, sumarle dicha cantidad
    							// hace ineficiente la funcion de aumentar la velocidad cuando la serpiente come. cabe
    							// destacar que tenemos el consentimiento del asistente de catedra Andres Chamorro para
    							// dejar los valores como se encuentran presentes.
    	
    	/*
    	 * (anthony) en el siguiente try / except implemente
    	 *  sonidos en los casos cuando la serpiente coma
    	 */
    	try { 
    		justAte = true;
    		switch (direction) {
    		case "Left":
    			handler.getGame().getMusicManager()	.playMusic("/music/eatFX4.wav");		
    			break;
    		case "Right":
    			handler.getGame().getMusicManager()	.playMusic("/music/eatFX3.wav");		
    			break;
    		case "Up":
    			handler.getGame().getMusicManager()	.playMusic("/music/eatFX1.wav");		
    			break;
    		case "Down":
    			handler.getGame().getMusicManager()	.playMusic("/music/eatFX2.wav");		
    			break;
    		default:
    			break;
    		}
    	} finally {
    	}
        length++;
        Tail tail= null;
        handler.getWorld().appleLocation[xCoord][yCoord]=false;
        handler.getWorld().appleOnBoard=false;
        
        switch (direction){
            case "Left":
                if( handler.getWorld().body.isEmpty()){
                    if(this.xCoord!=handler.getWorld().GridWidthHeightPixelCount-1){
                        tail = new Tail(this.xCoord+1,this.yCoord,handler);
                    }else{
                        if(this.yCoord!=0){
                            tail = new Tail(this.xCoord,this.yCoord-1,handler);
                        }else{
                            tail =new Tail(this.xCoord,this.yCoord+1,handler);
                        }
                    }
                }else{
                    if(handler.getWorld().body.getLast().x!=handler.getWorld().GridWidthHeightPixelCount-1){
                        tail=new Tail(handler.getWorld().body.getLast().x+1,this.yCoord,handler);
                    }else{
                        if(handler.getWorld().body.getLast().y!=0){
                            tail=new Tail(handler.getWorld().body.getLast().x,this.yCoord-1,handler);
                        }else{
                            tail=new Tail(handler.getWorld().body.getLast().x,this.yCoord+1,handler);
                        }
                    }
                }
                break;
            case "Right":
                if( handler.getWorld().body.isEmpty()){
                    if(this.xCoord!=0){
                        tail=new Tail(this.xCoord-1,this.yCoord,handler);
                    }else{
                        if(this.yCoord!=0){
                            tail=new Tail(this.xCoord,this.yCoord-1,handler);
                        }else{
                            tail=new Tail(this.xCoord,this.yCoord+1,handler);
                        }
                    }
                }else{
                    if(handler.getWorld().body.getLast().x!=0){
                        tail=(new Tail(handler.getWorld().body.getLast().x-1,this.yCoord,handler));
                    }else{
                        if(handler.getWorld().body.getLast().y!=0){
                            tail=(new Tail(handler.getWorld().body.getLast().x,this.yCoord-1,handler));
                        }else{
                            tail=(new Tail(handler.getWorld().body.getLast().x,this.yCoord+1,handler));
                        }
                    }
                }
                break;
            case "Up":
                if( handler.getWorld().body.isEmpty()){
                    if(this.yCoord!=handler.getWorld().GridWidthHeightPixelCount-1){
                        tail=(new Tail(this.xCoord,this.yCoord+1,handler));
                    }else{
                        if(this.xCoord!=0){
                            tail=(new Tail(this.xCoord-1,this.yCoord,handler));
                        }else{
                            tail=(new Tail(this.xCoord+1,this.yCoord,handler));
                        }
                    }
                }else{
                    if(handler.getWorld().body.getLast().y!=handler.getWorld().GridWidthHeightPixelCount-1){
                        tail=(new Tail(handler.getWorld().body.getLast().x,this.yCoord+1,handler));
                    }else{
                        if(handler.getWorld().body.getLast().x!=0){
                            tail=(new Tail(handler.getWorld().body.getLast().x-1,this.yCoord,handler));
                        }else{
                            tail=(new Tail(handler.getWorld().body.getLast().x+1,this.yCoord,handler));
                        }
                    }

                }
                break;
            case "Down":
                if( handler.getWorld().body.isEmpty()){
                    if(this.yCoord!=0){
                        tail=(new Tail(this.xCoord,this.yCoord-1,handler));
                    }else{
                        if(this.xCoord!=0){
                            tail=(new Tail(this.xCoord-1,this.yCoord,handler));
                        }else{
                            tail=(new Tail(this.xCoord+1,this.yCoord,handler));
                        } 
                    }
                }else{
                    if(handler.getWorld().body.getLast().y!=0){
                        tail=(new Tail(handler.getWorld().body.getLast().x,this.yCoord-1,handler));
                    }else{
                        if(handler.getWorld().body.getLast().x!=0){
                            tail=(new Tail(handler.getWorld().body.getLast().x-1,this.yCoord,handler));
                        }else{
                            tail=(new Tail(handler.getWorld().body.getLast().x+1,this.yCoord,handler));
                        }
                    }

                }
                break;
        }
        handler.getWorld().body.addLast(tail);
        handler.getWorld().playerLocation[tail.x][tail.y] = true; 
    }

    public void kill(){
        length = 0;
        for (int i = 0; i < handler.getWorld().GridWidthHeightPixelCount; i++) {
            for (int j = 0; j < handler.getWorld().GridWidthHeightPixelCount; j++) {
                handler.getWorld().playerLocation[i][j]=false;
               
            }
        }
	}
    
    public boolean isJustAte() {
        return justAte;
    }

    public void setJustAte(boolean justAte) {
        this.justAte = justAte;
    }
}

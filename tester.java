//Lea Lau-Ming
//ID: 260690496
import java.util.ArrayList;
import java.util.Arrays;
public class tester {
	public static void main(String[] args) {

		class HiRiQ
		{

			//int is used to reduce storage to a minimum...
			public int config;
			public byte weight;
			
			public ArrayList<HiRiQ> children;
			public String move;
			public HiRiQ parent;
			
			void setChildren(ArrayList<HiRiQ> children){
				this.children = children;
			}

			void setMove(String move){
				this.move = move;
			}
			
			void setParent(HiRiQ parent){
				this.parent = parent;
			}

			String getMove(){
				return move;
			}
			
			HiRiQ getParent(){
				return parent;
			}
			
			//initialize to one of 5 reachable START config n=0,1,2,3,4
			HiRiQ(byte n)
			{
				if (n==0)
				{config=65536/2;weight=1;}
				else
					if (n==1)
					{config=1626;weight=6;}
					else
						if (n==2)
						{config=-1140868948; weight=10;}
						else
							if (n==3)
							{config=-411153748; weight=13;}
							else
							{config=-2147450879; weight=32;}
			}

			boolean IsSolved()
			{
				return( (config==65536/2) && (weight==1) );
			}

			//transforms the array of 33 booleans to an (int) config and a (byte) weight.
			public void store(boolean[] B)
			{
				int a=1;
				config=0;
				weight=(byte) 0;
				if (B[0]) {weight++;}
				for (int i=1; i<32; i++)
				{
					if (B[i]) {config=config+a;weight++;}
					a=2*a;
				}
				if (B[32]) {config=-config;weight++;}
			}

			//transform the int representation to an array of booleans.
			//the weight (byte) is necessary because only 32 bits are memorized
			//and so the 33rd is decided based on the fact that the config has the
			//correct weight or not.
			public boolean[] load(boolean[] B)
			{
				byte count=0;
				int fig=config;
				B[32]=fig<0;
				if (B[32]) {fig=-fig;count++;}
				int a=2;
				for (int i=1; i<32; i++)
				{
					B[i]= fig%a>0;
					if (B[i]) {fig=fig-a/2;count++;}
					a=2*a;
				}
				B[0]= count<weight;
				return(B);
			}

			//prints the int representation to an array of booleans.
			//the weight (byte) is necessary because only 32 bits are memorized
			//and so the 33rd is decided based on the fact that the config has the
			//correct weight or not.
			public void printB(boolean Z)
			{if (Z) {System.out.print("[ ]");} else {System.out.print("[@]");}}

			public void print()
			{
				byte count=0;
				int fig=config;
				boolean next,last=fig<0;
				if (last) {fig=-fig;count++;}
				int a=2;
				for (int i=1; i<32; i++)
				{
					next= fig%a>0;
					if (next) {fig=fig-a/2;count++;}
					a=2*a;
				}
				next= count<weight;

				count=0;
				fig=config;
				if (last) {fig=-fig;count++;}
				a=2;
				System.out.print("      ") ; printB(next);
				for (int i=1; i<32; i++)
				{
					next= fig%a>0;
					if (next) {fig=fig-a/2;count++;}
					a=2*a;
					printB(next);
					if (i==2 || i==5 || i==12 || i==19 || i==26 || i==29) {System.out.println() ;}
					if (i==2 || i==26 || i==29) {System.out.print("      ") ;};
				}
				printB(last); System.out.println() ;

			}

			//Find all the children from boolean[] b
			ArrayList<HiRiQ> substitutionTriplets(boolean[] b){
				
				int i = 0;
				ArrayList<HiRiQ> child = new ArrayList<HiRiQ>();
				
				//for all triplets
				for(i=0; i<38; i++){
					//make a copy of the array b
					boolean[] copy = new boolean[33];
					for(int j=0; j<b.length; j++){
						copy[j]=b[j];
					}
					//set parent
					HiRiQ root = new HiRiQ((byte)0);
					root.store(b);
					//initialize move and HiRiQ to put in the array list
					String move = "";
					HiRiQ x = new HiRiQ((byte)0);
					//initialize the triplets
					boolean[][] triplets = {{b[0], b[1], b[2]}, {b[3], b[4], b[5]}, {b[6], b[7], b[8]}, {b[7], b[8], b[9]}, {b[8], b[9], b[10]}, {b[9], b[10], b[11]}, {b[10], b[11], b[12]}, {b[13], b[14], b[15]}, {b[14], b[15], b[16]}, {b[15], b[16], b[17]}, {b[16], b[17], b[18]}, {b[17], b[18], b[19]}, {b[20], b[21], b[22]}, {b[21], b[22], b[23]}, {b[22], b[23], b[24]}, {b[23], b[24], b[25]}, {b[24], b[25], b[26]}, {b[27], b[28], b[29]}, {b[30], b[31], b[32]}, {b[12], b[19], b[26]}, {b[11], b[18], b[25]}, {b[2], b[5], b[10]}, {b[5], b[10], b[17]}, {b[10], b[17], b[24]}, {b[17], b[24], b[29]}, {b[24], b[29], b[32]}, {b[1], b[4], b[9]}, {b[4], b[9], b[16]}, {b[9], b[16], b[23]}, {b[16], b[23], b[28]}, {b[23], b[28], b[31]}, {b[0], b[3], b[8]}, {b[3], b[8], b[15]}, {b[8], b[15], b[22]}, {b[15], b[22], b[27]}, {b[22], b[27], b[30]}, {b[7], b[14], b[21]}, {b[6], b[13], b[20]}};
					//substitute the triplets
					if(triplets[i][0] == false && triplets[i][1] == false && triplets[i][2] == true){
						if(i==0){
							copy[0]=true;
							copy[1]=true;
							copy[2]=false;
							move = "0@2 ";
						}
						else if(i==1){
							copy[3]=true;
							copy[4]=true;
							copy[5]=false;
							move = "3@5 ";
						}
						else if(i==2){
							copy[6]=true;
							copy[7]=true;
							copy[8]=false;
							move = "6@8 ";
						}
						else if(i==3){
							copy[7]=true;
							copy[8]=true;
							copy[9]=false;
							move = "7@9 ";
						}
						else if(i==4){
							copy[8]=true;
							copy[9]=true;
							copy[10]=false;
							move = "8@10 ";
						}
						else if(i==5){
							copy[9]=true;
							copy[10]=true;
							copy[11]=false;
							move="9@11 ";
						}
						else if(i==6){
							copy[10]=true;
							copy[11]=true;
							copy[12]=false;
							move = "10@12 ";
						}
						else if(i==7){
							copy[13]=true;
							copy[14]=true;
							copy[15]=false;
							move = "13@15 ";
						}
						else if(i==8){
							copy[14]=true;
							copy[15]=true;
							copy[16]=false;
							move = "14@16 ";
						}
						else if(i==9){
							copy[15]=true;
							copy[16]=true;
							copy[17]=false;
							move = "15@17 ";
						}
						else if(i==10){
							copy[16]=true;
							copy[17]=true;
							copy[18]=false;
							move = "16@18 ";
						}
						else if(i==11){
							copy[17]=true;
							copy[18]=true;
							copy[19]=false;
							move = "17@19 ";
						}
						else if(i==12){
							copy[20]=true;
							copy[21]=true;
							copy[22]=false;
							move = "20@22 ";
						}
						else if(i==13){
							copy[21]=true;
							copy[22]=true;
							copy[23]=false;
							move = "21@23 ";
						}
						else if(i==14){
							copy[22]=true;
							copy[23]=true;
							copy[24]=false;
							move = "22@24 ";
						}
						else if(i==15){
							copy[23]=true;
							copy[24]=true;
							copy[25]=false;
							move = "23@25 ";
						}
						else if(i==16){
							copy[24]=true;
							copy[25]=true;
							copy[26]=false;
							move = "24@26 ";
						}
						else if(i==17){
							copy[27]=true;
							copy[28]=true;
							copy[29]=false;
							move = "27@29 ";
						}
						else if(i==18){
							copy[30]=true;
							copy[31]=true;
							copy[32]=false;
							move = "30@32 ";
						}
						else if(i==19){
							copy[12]=true;
							copy[19]=true;
							copy[26]=false;
							move = "12@26 ";
						}
						else if(i==20){
							copy[11]=true;
							copy[18]=true;
							copy[25]=false;
							move = "11@25 ";
						}
						else if(i==21){
							copy[2]=true;
							copy[5]=true;
							copy[10]=false;
							move = "2@10 ";
						}
						else if(i==22){
							copy[5]=true;
							copy[10]=true;
							copy[17]=false;
							move = "5@17 ";
						}
						else if(i==23){
							copy[10]=true;
							copy[17]=true;
							copy[24]=false;
							move = "10@24 ";
						}
						else if(i==24){
							copy[17]=true;
							copy[24]=true;
							copy[29]=false;
							move = "17@29 ";
						}
						else if(i==25){
							copy[24]=true;
							copy[29]=true;
							copy[32]=false;
							move = "24@32 ";
						}
						else if(i==26){
							copy[1]=true;
							copy[4]=true;
							copy[9]=false;
							move = "1@9 ";
						}
						else if(i==27){
							copy[4]=true;
							copy[9]=true;
							copy[16]=false;
							move = "4@16 ";
						}
						else if(i==28){
							copy[9]=true;
							copy[16]=true;
							copy[23]=false;
							move = "9@23 ";
						}
						else if(i==29){
							copy[16]=true;
							copy[23]=true;
							copy[28]=false;
							move = "16@28 ";
						}
						else if(i==30){
							copy[23]=true;
							copy[28]=true;
							copy[31]=false;
							move = "23@31 ";
						}
						else if(i==31){
							copy[0]=true;
							copy[3]=true;
							copy[8]=false;
							move = "0@8 ";
						}
						else if(i==32){
							copy[3]=true;
							copy[8]=true;
							copy[15]=false;
							move = "3@15 ";
						}
						else if(i==33){
							copy[8]=true;
							copy[15]=true;
							copy[22]=false;
							move = "8@22 ";
						}
						else if(i==34){
							copy[15]=true;
							copy[22]=true;
							copy[27]=false;
							move = "15@27 ";
						}
						else if(i==35){
							copy[22]=true;
							copy[27]=true;
							copy[30]=false;
							move = "22@30 ";
						}
						else if(i==36){
							copy[7]=true;
							copy[14]=true;
							copy[21]=false;
							move = "7@21 ";
						}
						else if(i==37){
							copy[6]=true;
							copy[13]=true;
							copy[20]=false;
							move = "6@20 ";
						}
						//transform copy into HiRiQ
						x.store(copy);
						//put it in the array list
						child.add(x);
						//set move
						x.setMove(move);
						x.setParent(root);
						//if the HiRiQ is the solved puzzle then break
						if(x.IsSolved() == true) {
							break;
						}
					}
					else if(triplets[i][0] == true && triplets[i][1] == true && triplets[i][2] == false){
						if(i==0){
							copy[0]=false;
							copy[1]=false;
							copy[2]=true;
							move = "0@2 ";
						}
						else if(i==1){
							copy[3]=false;
							copy[4]=false;
							copy[5]=true;
							move = "3@5 ";
						}
						else if(i==2){
							copy[6]=false;
							copy[7]=false;
							copy[8]=true;
							move = "6@8 ";
						}
						else if(i==3){
							copy[7]=false;
							copy[8]=false;
							copy[9]=true;
							move = "7@9 ";
						}
						else if(i==4){
							copy[8]=false;
							copy[9]=false;
							copy[10]=true;
							move = "8@10 ";
						}
						else if(i==5){
							copy[9]=false;
							copy[10]=false;
							copy[11]=true;
							move = "9@11 ";
						}
						else if(i==6){
							copy[10]=false;
							copy[11]=false;
							copy[12]=true;
							move = "10@12 ";
						}
						else if(i==7){
							copy[13]=false;
							copy[14]=false;
							copy[15]=true;
							move = "13@15 ";
						}
						else if(i==8){
							copy[14]=false;
							copy[15]=false;
							copy[16]=true;
							move = "14@16 ";
						}
						else if(i==9){
							copy[15]=false;
							copy[16]=false;
							copy[17]=true;
							move = "15@17 ";
						}
						else if(i==10){
							copy[16]=false;
							copy[17]=false;
							copy[18]=true;
							move = "16@18 ";
						}
						else if(i==11){
							copy[17]=false;
							copy[18]=false;
							copy[19]=true;
							move = "17@19 ";
						}
						else if(i==12){
							copy[20]=false;
							copy[21]=false;
							copy[22]=true;
							move = "20@22 ";
						}
						else if(i==13){
							copy[21]=false;
							copy[22]=false;
							copy[23]=true;
							move = "21@23 ";
						}
						else if(i==14){
							copy[22]=false;
							copy[23]=false;
							copy[24]=true;
							move = "22@24 ";
						}
						else if(i==15){
							copy[23]=false;
							copy[24]=false;
							copy[25]=true;
							move = "23@25 ";
						}
						else if(i==16){
							copy[24]=false;
							copy[25]=false;
							copy[26]=true;
							move = "24@26 ";
						}
						else if(i==17){
							copy[27]=false;
							copy[28]=false;
							copy[29]=true;
							move = "27@29 ";
						}
						else if(i==18){
							copy[30]=false;
							copy[31]=false;
							copy[32]=true;
							move = "30@32 ";
						}
						else if(i==19){
							copy[12]=false;
							copy[19]=false;
							copy[26]=true;
							move = "19@26 ";
						}
						else if(i==20){
							copy[11]=false;
							copy[18]=false;
							copy[25]=true;
							move = "11@25 ";
						}
						else if(i==21){
							copy[2]=false;
							copy[5]=false;
							copy[10]=true;
							move = "2@10 ";
						}
						else if(i==22){
							copy[5]=false;
							copy[10]=false;
							copy[17]=true;
							move = "5@17 ";
						}
						else if(i==23){
							copy[10]=false;
							copy[17]=false;
							copy[24]=true;
							move = "10@24 ";
						}
						else if(i==24){
							copy[17]=false;
							copy[24]=false;
							copy[29]=true;
							move = "17@29 ";
						}
						else if(i==25){
							copy[24]=false;
							copy[29]=false;
							copy[32]=true;
							move = "24@32 ";
						}
						else if(i==26){
							copy[1]=false;
							copy[4]=false;
							copy[9]=true;
							move = "1@9 ";
						}
						else if(i==27){
							copy[4]=false;
							copy[9]=false;
							copy[16]=true;
							move = "4@16 ";
						}
						else if(i==28){
							copy[9]=false;
							copy[16]=false;
							copy[23]=true;
							move = "9@23 ";
						}
						else if(i==29){
							copy[16]=false;
							copy[23]=false;
							copy[28]=true;
							move = "16@28 ";
						}
						else if(i==30){
							copy[23]=false;
							copy[28]=false;
							copy[31]=true;
							move = "23@31 ";
						}
						else if(i==31){
							copy[0]=false;
							copy[3]=false;
							copy[8]=true;
							move = "0@8 ";
						}
						else if(i==32){
							copy[3]=false;
							copy[8]=false;
							copy[15]=true;
							move = "3@15 ";
						}
						else if(i==33){
							copy[8]=false;
							copy[15]=false;
							copy[22]=true;
							move = "8@22 ";
						}
						else if(i==34){
							copy[15]=false;
							copy[22]=false;
							copy[27]=true;
							move = "15@27 ";
						}
						else if(i==35){
							copy[22]=false;
							copy[27]=false;
							copy[30]=true;
							move = "22@30 ";
						}
						else if(i==36){
							copy[7]=false;
							copy[14]=false;
							copy[21]=true;
							move = "7@21 ";
						}
						else if(i==37){
							copy[6]=false;
							copy[13]=false;
							copy[20]=true;
							move = "6@20 ";
						}
						//transform copy into HiRiQ
						x.store(copy);
						//put it in the array list
						child.add(x);
						//set move
						x.setMove(move);
						x.setParent(root);
						//if the HiRiQ is the solved puzzle then break
						if(x.IsSolved() == true) {
							break;
						}
					}
					else if(triplets[i][0] == false && triplets[i][1] == true && triplets[i][2] == true){
						if(i==0){
							copy[0]=true;
							copy[1]=false;
							copy[2]=false;
							move = "0@2 ";
						}
						else if(i==1){
							copy[3]=true;
							copy[4]=false;
							copy[5]=false;
							move = "3@4 ";
						}
						else if(i==2){
							copy[6]=true;
							copy[7]=false;
							copy[8]=false;
							move = "6@8 ";
						}
						else if(i==3){
							copy[7]=true;
							copy[8]=false;
							copy[9]=false;
							move = "7@9 ";
						}
						else if(i==4){
							copy[8]=true;
							copy[9]=false;
							copy[10]=false;
							move = "8@10 ";
						}
						else if(i==5){
							copy[9]=true;
							copy[10]=false;
							copy[11]=false;
							move = "9@11 ";
						}
						else if(i==6){
							copy[10]=true;
							copy[11]=false;
							copy[12]=false;
							move = "10@12 ";
						}
						else if(i==7){
							copy[13]=true;
							copy[14]=false;
							copy[15]=false;
							move = "13@15 ";
						}
						else if(i==8){
							copy[14]=true;
							copy[15]=false;
							copy[16]=false;
							move = "14@16 ";
						}
						else if(i==9){
							copy[15]=true;
							copy[16]=false;
							copy[17]=false;
							move = "15@17 ";
						}
						else if(i==10){
							copy[16]=true;
							copy[17]=false;
							copy[18]=false;
							move = "16@18 ";
						}
						else if(i==11){
							copy[17]=true;
							copy[18]=false;
							copy[19]=false;
							move = "17@19 ";
						}
						else if(i==12){
							copy[20]=true;
							copy[21]=false;
							copy[22]=false;
							move = "20@22 ";
						}
						else if(i==13){
							copy[21]=true;
							copy[22]=false;
							copy[23]=false;
							move = "21@23 ";
						}
						else if(i==14){
							copy[22]=true;
							copy[23]=false;
							copy[24]=false;
							move = "22@24 ";
						}
						else if(i==15){
							copy[23]=true;
							copy[24]=false;
							copy[25]=false;
							move = "23@25 ";
						}
						else if(i==16){
							copy[24]=true;
							copy[25]=false;
							copy[26]=false;
							move = "24@26 ";
						}
						else if(i==17){
							copy[27]=true;
							copy[28]=false;
							copy[29]=false;
							move = "27@29 ";
						}
						else if(i==18){
							copy[30]=true;
							copy[31]=false;
							copy[32]=false;
							move = "30@32 ";
						}
						else if(i==19){
							copy[12]=true;
							copy[19]=false;
							copy[26]=false;
							move = "12@26 ";
						}
						else if(i==20){
							copy[11]=true;
							copy[18]=false;
							copy[25]=false;
							move = "11@25 ";
						}
						else if(i==21){
							copy[2]=true;
							copy[5]=false;
							copy[10]=false;
							move = "2@10 ";
						}
						else if(i==22){
							copy[5]=true;
							copy[10]=false;
							copy[17]=false;
							move = "5@17 ";
						}
						else if(i==23){
							copy[10]=true;
							copy[17]=false;
							copy[24]=false;
							move = "10@24 ";
						}
						else if(i==24){
							copy[17]=true;
							copy[24]=false;
							copy[29]=false;
							move = "17@29 ";
						}
						else if(i==25){
							copy[24]=true;
							copy[29]=false;
							copy[32]=false;
							move = "24@32 ";
						}
						else if(i==26){
							copy[1]=true;
							copy[4]=false;
							copy[9]=false;
							move = "1@9 ";
						}
						else if(i==27){
							copy[4]=true;
							copy[9]=false;
							copy[16]=false;
							move = "4@16 ";
						}
						else if(i==28){
							copy[9]=true;
							copy[16]=false;
							copy[23]=false;
							move = "9@23 ";
						}
						else if(i==29){
							copy[16]=true;
							copy[23]=false;
							copy[28]=false;
							move = "16@28 ";
						}
						else if(i==30){
							copy[23]=true;
							copy[28]=false;
							copy[31]=false;
							move = "23@31 ";
						}
						else if(i==31){
							copy[0]=true;
							copy[3]=false;
							copy[8]=false;
							move = "0@8 ";
						}
						else if(i==32){
							copy[3]=true;
							copy[8]=false;
							copy[15]=false;
							move = "3@15 ";
						}
						else if(i==33){
							copy[8]=true;
							copy[15]=false;
							copy[22]=false;
							move = "8@22 ";
						}
						else if(i==34){
							copy[15]=true;
							copy[22]=false;
							copy[27]=false;
							move = "15@27 ";
						}
						else if(i==35){
							copy[22]=true;
							copy[27]=false;
							copy[30]=false;
							move = "22@30 ";
						}
						else if(i==36){
							copy[7]=true;
							copy[14]=false;
							copy[21]=false;
							move = "7@21 ";
						}
						else if(i==37){
							copy[6]=true;
							copy[13]=false;
							copy[20]=false;
							move = "6@20 ";
						}
						//transform copy into HiRiQ
						x.store(copy);
						//put it in the array list
						child.add(x);
						//set move
						x.setMove(move);
						x.setParent(root);
						//if the HiRiQ is the solved puzzle then break
						if(x.IsSolved() == true) {
							break;
						}
					}
					else if(triplets[i][0] == true && triplets[i][1] == false && triplets[i][2] == false){
						if(i==0){
							copy[0]=false;
							copy[1]=true;
							copy[2]=true;
							move = "0@2 ";
						}
						else if(i==1){
							copy[3]=false;
							copy[4]=true;
							copy[5]=true;
							move = "3@5 ";
						}
						else if(i==2){
							copy[6]=false;
							copy[7]=true;
							copy[8]=true;
							move = "6@8 ";
						}
						else if(i==3){
							copy[7]=false;
							copy[8]=true;
							copy[9]=true;
							move = "7@9 ";
						}
						else if(i==4){
							copy[8]=false;
							copy[9]=true;
							copy[10]=true;
							move = "8@10 ";
						}
						else if(i==5){
							copy[9]=false;
							copy[10]=true;
							copy[11]=true;
							move = "9@11 ";
						}
						else if(i==6){
							copy[10]=false;
							copy[11]=true;
							copy[12]=true;
							move = "10@12 ";
						}
						else if(i==7){
							copy[13]=false;
							copy[14]=true;
							copy[15]=true;
							move = "13@15 ";
						}
						else if(i==8){
							copy[14]=false;
							copy[15]=true;
							copy[16]=true;
							move = "14@16 ";
						}
						else if(i==9){
							copy[15]=false;
							copy[16]=true;
							copy[17]=true;
							move = "15@17 ";
						}
						else if(i==10){
							copy[16]=false;
							copy[17]=true;
							copy[18]=true;
							move = "16@18 ";
						}
						else if(i==11){
							copy[17]=false;
							copy[18]=true;
							copy[19]=true;
							move = "17@19 ";
						}
						else if(i==12){
							copy[20]=false;
							copy[21]=true;
							copy[22]=true;
							move = "20@22 ";
						}
						else if(i==13){
							copy[21]=false;
							copy[22]=true;
							copy[23]=true;
							move = "21@23 ";
						}
						else if(i==14){
							copy[22]=false;
							copy[23]=true;
							copy[24]=true;
							move = "22@24 ";
						}
						else if(i==15){
							copy[23]=false;
							copy[24]=true;
							copy[25]=true;
							move = "23@25 ";
						}
						else if(i==16){
							copy[24]=false;
							copy[25]=true;
							copy[26]=true;
							move = "24@26 ";
						}
						else if(i==17){
							copy[27]=false;
							copy[28]=true;
							copy[29]=true;
							move = "27@29 ";
						}
						else if(i==18){
							copy[30]=false;
							copy[31]=true;
							copy[32]=true;
							move = "30@32 ";
						}
						else if(i==19){
							copy[12]=false;
							copy[19]=true;
							copy[26]=true;
							move = "12@26 ";
						}
						else if(i==20){
							copy[11]=false;
							copy[18]=true;
							copy[25]=true;
							move = "11@25 ";
						}
						else if(i==21){
							copy[2]=false;
							copy[5]=true;
							copy[10]=true;
							move = "2@10 ";
						}
						else if(i==22){
							copy[5]=false;
							copy[10]=true;
							copy[17]=true;
							move = "5@17 ";
						}
						else if(i==23){
							copy[10]=false;
							copy[17]=true;
							copy[24]=true;
							move = "10@24 ";
						}
						else if(i==24){
							copy[17]=false;
							copy[24]=true;
							copy[29]=true;
							move = "17@29 ";
						}
						else if(i==25){
							copy[24]=false;
							copy[29]=true;
							copy[32]=true;
							move = "24@32 ";
						}
						else if(i==26){
							copy[1]=false;
							copy[4]=true;
							copy[9]=true;
							move = "1@9 ";
						}
						else if(i==27){
							copy[4]=false;
							copy[9]=true;
							copy[16]=true;
							move = "4@16 ";
						}
						else if(i==28){
							copy[9]=false;
							copy[16]=true;
							copy[23]=true;
							move = "9@23 ";
						}
						else if(i==29){
							copy[16]=false;
							copy[23]=true;
							copy[28]=true;
							move = "16@28 ";
						}
						else if(i==30){
							copy[23]=false;
							copy[28]=true;
							copy[31]=true;
							move = "23@31 ";
						}
						else if(i==31){
							copy[0]=false;
							copy[3]=true;
							copy[8]=true;
							move = "0@8 ";
						}
						else if(i==32){
							copy[3]=false;
							copy[8]=true;
							copy[15]=true;
							move = "3@15 ";
						}
						else if(i==33){
							copy[8]=false;
							copy[15]=true;
							copy[22]=true;
							move = "8@22 ";
						}
						else if(i==34){
							copy[15]=false;
							copy[22]=true;
							copy[27]=true;
							move = "15@27 ";
						}
						else if(i==35){
							copy[22]=false;
							copy[27]=true;
							copy[30]=true;
							move = "22@30 ";
						}
						else if(i==36){
							copy[7]=false;
							copy[14]=true;
							copy[21]=true;
							move = "7@21 ";
						}
						else if(i==37){
							copy[6]=false;
							copy[13]=true;
							copy[20]=true;
							move = "6@20 ";
						}
						//transform copy into HiRiQ
						x.store(copy);
						//put it in the array list
						child.add(x);
						//set move
						x.setMove(move);
						x.setParent(root);
						//if the HiRiQ is the solved puzzle then exit the loop and return the child array list
						if(x.IsSolved() == true) {
							break;
						}
					}
				}
				return child;
			}			
			
			//Solve the puzzle! Based of BFS
			String solve(boolean[] b){
				//Initialize the head/root
				String result="@";
				HiRiQ head = new HiRiQ((byte)0);
				//head is b in type HiRiQ
				head.store(b);
				head.setParent(null);
				
				//if it's not already solved do:
				if(head.IsSolved() == false){
					//initialize queue and currVertex
					ArrayList<HiRiQ> queue = new ArrayList<HiRiQ>();
					queue.add(head);
					//create a pointer
					HiRiQ pointer = new HiRiQ((byte)0);
					pointer = head;
					//while the currVertex is not the solved puzzle do...
					int counter=0;
					int a=0;
					
					while(pointer.IsSolved()==false){
						//initialize currVertex and remove it from the queue
						HiRiQ currVertex = new HiRiQ((byte)0);
						currVertex = queue.get(counter);
						counter++;
						pointer = currVertex;
						
						//add the children of the currVertex into the queue
						//put a limit to the queue
						if(a == 0){
							ArrayList<HiRiQ> child = new ArrayList<HiRiQ>();
							boolean[] B=new boolean[33];
							currVertex.load(B);
							child = currVertex.substitutionTriplets(B);
							currVertex.setChildren(child);
							//for every child , set the parent and put it in the queue
							for(int i=0; i<child.size(); i++){
								child.get(i).setParent(currVertex);
									
								//if the child we are going to put in the queue is the solved puzzle, make the pointer point to the solved puzzle
								if(child.get(i).IsSolved() == true){
									pointer = child.get(i);
									a = 4;
									break;
								}
									
								queue.add(child.get(i));
							}

						}
						
					}
					result = getString(pointer);
					
				}
				return result;
			}
			
			//get all the moves from unsolved to solved
			String getString(HiRiQ solved){
				String result = "";
				//while there's still a parent to the HiRiQ get the move
				while (solved.getParent() != null){
					result = solved.getMove() + result;
					solved = solved.getParent();
				}
				return result;
			}

		}



		boolean[] B=new boolean[33];
		HiRiQ W=new HiRiQ((byte) 0) ;
		//W.print(); System.out.println(W.IsSolved());
		HiRiQ X=new HiRiQ((byte) 1) ;
		//X.print(); System.out.println(X.IsSolved());
		HiRiQ Y=new HiRiQ((byte) 2) ;
		//Y.print(); System.out.println(Y.IsSolved());
		HiRiQ Z=new HiRiQ((byte) 3) ;
		//Z.print(); System.out.println(Z.IsSolved());
		HiRiQ V=new HiRiQ((byte) 4) ;
		//V.print(); System.out.println(V.IsSolved());
		
		boolean[] essai = {false, false, false, false, true, false, false, false, false, false, false, false, false, true, true, false, false, false, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false};
		
		
		System.out.println(X.solve(essai));
		
		
	}
}

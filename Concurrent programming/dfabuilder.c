#include <stdlib.h>
#include <stdio.h>
#include <time.h>
#include <omp.h>

/* Size of the DFA */
#define MAXSTATES 5
/* Number of characters in the alphabet */
#define ALPHABETSIZE 4
/* Size of the string to match against.  You may need to adjust this. */
#define STRINGSIZE 100000000

/* State transition table (ie the DFA) */
int stateTable[MAXSTATES][ALPHABETSIZE];

/* Initialize the table */
void initTable() {
    int start = 0;
    int accept = 3;
    int reject = 4;

    /* Note that characters values are assumed to be 0-based. */
    stateTable[0][0] = 1;      
    stateTable[0][1] = reject; 
    stateTable[0][2] = reject; 
    stateTable[0][3] = reject; 
    
    stateTable[1][0] = 1;      
    stateTable[1][1] = 2;      
    stateTable[1][2] = reject; 
    stateTable[1][3] = reject; 
    
    stateTable[2][0] = reject; 
    stateTable[2][1] = 2;      
    stateTable[2][2] = 3;      
    stateTable[2][3] = 3;      
    
    stateTable[3][0] = 1;      
    stateTable[3][1] = reject; 
    stateTable[3][2] = 3;      
    stateTable[3][3] = 3;      
    
    // reject state
    stateTable[4][0] = reject; 
    stateTable[4][1] = reject; 
    stateTable[4][2] = reject; 
    stateTable[4][3] = reject; 
}


/* Construct a sample string to match against.  Note that this uses characters, encoded in ASCII,
   so to get 0-based characters you'd need to subtract 'a'. */
char *buildString() {
    int i;
    char *s = (char *)malloc(sizeof(char)*(STRINGSIZE));
    if (s==NULL) {
        printf("\nOut of memory!\n");
        exit(1);
    }
    int max = STRINGSIZE-3;

    /* seed the rnd generator (use a fixed number rather than the time for testing) */
    srand((unsigned int)time(NULL)); 

    /* And build a long string that might actually match */
    int j=0;
    while(j<max) {
        s[j++] = 'a';
        while (rand()%1000<997 && j<max) 
            s[j++] = 'a';
        if (j<max)
            s[j++] = 'b';
        while (rand()%1000<997 && j<max) 
            s[j++] = 'b';
        if (j<max)
            s[j++] = (rand()%2==1) ? 'c' : 'd';
        while (rand()%1000<997 && j<max) 
            s[j++] = (rand()%2==1) ? 'c' : 'd';
    }
    s[max] = 'a';
    s[max+1] = 'b';
    s[max+2] = (rand()%2==1) ? 'c' : 'd';
    return s;
}

int main(int argc, char *argv[]) {
    
    if(argc <= 1){
        return -1;
    }
    
    initTable();
    char *s = buildString();
    printf("Built: [0]=%c and [%d]=%c\n",s[0],(int)(STRINGSIZE-1),s[STRINGSIZE-1]);
    int n = atoi(argv[1])+1;
    printf("Using %d optimistic threads\n", n-1);
    int results[n][4]; // store results of optimistic threads
    clock_t start, endt;
    omp_set_nested(1); // allow nested parallel 
    
    #pragma omp parallel num_threads(n) 
    {
        int startingIndex = omp_get_thread_num()*STRINGSIZE/n;
        int end = startingIndex+STRINGSIZE/n;
        if(omp_get_thread_num() == n-1){
            end = STRINGSIZE;
        }
        
        start = clock();
        // for master 
        if(omp_get_thread_num() == 0){
            int nextIndex = 0;
            for(int i=startingIndex; i<end; i++){
                char character = s[i];
                nextIndex = stateTable[nextIndex][(int)(character)-97];
                if(nextIndex == 4){
                        break;
                    }
            }
            results[omp_get_thread_num()][0] = nextIndex;
        }
        // for optimistic threads
        else{
            int id = omp_get_thread_num();
            #pragma omp parallel num_threads(4)
            {  
                int nextIndex = omp_get_thread_num();
                for(int j=startingIndex; j<end; j++){
                    char character = s[j];
                    nextIndex = stateTable[nextIndex][(int)(character)-97];
                    if(nextIndex == 4){
                        break;
                    }
                }
                results[id][omp_get_thread_num()] = nextIndex;
            }
        }
    }
    
  
    // matching process
    int finalAnswer = results[0][0];
    for(int i=1; i<n; i++){
        finalAnswer = results[i][finalAnswer];
        if(finalAnswer == 4){
            finalAnswer = 4;
            break; 
        }
    }
    endt = clock();
    printf("Final answer: %d\n", finalAnswer);
    printf("It took: %f ms\n", ((float)(endt - start) / 1000000.0F ) * 1000);
    return 0;
}

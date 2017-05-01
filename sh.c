//ID 260690496
//Name Léa Lau-Ming
#include <limits.h>
#include <sys/wait.h>
#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include <stdlib.h>
#include <signal.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

pid_t pidc2 = -1;
pid_t pidc = -1;
int jobs[50] = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};

//search for the first empty space in array
int searchNull(){
	for(int i = 0; i<50; i++){
		if(jobs[i] == -1){
			return i;
		}	
	}
	printf("no more space!");
	exit(-1);
}

//remove the pid 
int removePid(int oldpid){
	for(int i = 0; i<50; i++){
		if(jobs[i] == oldpid){
			jobs[i] = -1;
		}
	}
	return 1;
}

int getcmd(char *prompt, char *args[], int *background){
	int length, i = 0;
	char *token, *loc;
	char *line = NULL;
	size_t linecap = 0;
	token = calloc(50,sizeof(char));
	line = calloc(50,sizeof(char));
	
	//Verify error for malloc
	if (token == NULL || line == NULL){
		perror("calloc");
		exit(1);
	}

	printf("%s", prompt);
	/*&line address of the first char position where the input string will be stored, line has the full string */
	length = getline(&line, &linecap, stdin);

	if (length <= 0){
		exit(-1);
	}
	/*if you find a & in the input line, */
	if ((loc = index(line, '&')) != NULL){
		*background = 1;
		*loc = ' ';
	}
	else{
		*background = 0;
	}
	
	/*strsep extract token from string, get the string before \t\n in token*/
	while ((token = strsep(&line, " \t\n")) != NULL){
		for (int j = 0; j<strlen(token); j++){
			if (token[j] <= 32){
				token[j] = '\0';
			}
		}
		if (strlen(token) > 0){
			args[i++] = token;
		}
	}
	args[i] = '\0';
	free(line);
	free(token);
	return i;
}

static void sigHandler(int sig){
	//what control + c does, kill the child then the parent
	if(sig == SIGINT){
		kill(pidc, SIGKILL);
		kill(getpid(), SIGKILL);
	}
	//quit shell
	if(sig == SIGQUIT){
		signal(SIGQUIT, SIG_DFL);
		kill(pidc, SIGQUIT);
		kill(getpid(), SIGQUIT);
	}
	//when a child returns
	if(sig == SIGCHLD){
		removePid(pidc);
	}

	if(sig == SIGTSTP){
		int status;
		kill(pidc2, SIGSTOP);
		kill(pidc2, SIGCONT);
		pid_t pid = waitpid(pidc2, &status, 0);
	}
}

int main(void){
	char *args[20];
	int bg;
	int fd;
	int stdout_copy = dup(STDOUT_FILENO);
	int dup_fd;
	
	//handling signal error
	if(signal(SIGINT, sigHandler) == SIG_ERR || signal(SIGQUIT, sigHandler) == SIG_ERR || signal(SIGCHLD, sigHandler) == SIG_ERR){ 
		printf("Signal Error\n");
		exit(1);
	}
	int p[2];
	while (1){
		//pipe error
		if(pipe(p) == -1){
			printf("Pipe error!");
			exit(EXIT_FAILURE);
		}
		signal(SIGINT, sigHandler);
		signal(SIGTSTP, SIG_IGN);
		signal(SIGCHLD, sigHandler);	

		bg = 0;
		int cnt = getcmd("\n>> ", args, &bg);

		if(args[0] != NULL){
			
			//built-in cd command
			if(strcmp(args[0],"cd") == 0 && args[1] != NULL){
				if(chdir(args[1]) == 0){
					printf("directory changed.");
				}
				else{
					printf("no such directory");
				}
			}
			//built-in pwd command
			else if(strcmp(args[0],"pwd") == 0 && args[1] == NULL){
				char buffer[PATH_MAX +1];
				char* path =(char*) malloc(PATH_MAX+1);
				path = getcwd(buffer, PATH_MAX+1);
				if(path != NULL){
					printf("%s", path);
				}
				else{
					perror("pwd");
					exit(EXIT_FAILURE);
				}
			}
			//built-in jobs command
			else if(strcmp(args[0],"jobs") == 0 && args[1] == NULL){
				for(int i = 0; i<50; i++){
					if(jobs[i] != -1){
						printf("%d   %d \n",i,jobs[i]);
					}
				}
			}
			//built-in fg command
			else if(strcmp(args[0],"fg") == 0 && args[1] != NULL && args[2] == NULL){
				//convert char pointer to int
				char *c = args[1];
				int x = atoi(c);
				pidc2 = jobs[x];
				jobs[x] = -1;
				signal(SIGTSTP, sigHandler);
				raise(SIGTSTP);
			}
			//fork
			else{
				//simple output redirection
				if(args[0] != NULL && args[1] != NULL && strcmp(args[1],">") == 0 && args[2] != NULL){
					fd = open(args[2], O_CREAT | O_WRONLY, S_IRUSR | S_IWUSR);
					close(STDOUT_FILENO);
					dup_fd = dup(fd);
				}
				pidc = fork();
				//if fork fails
				if(pidc == (pid_t)-1){
					perror("fork");
					exit(EXIT_FAILURE);
				}
				//child process
				else if(pidc == (pid_t)0){
					if(args[0] != NULL && args[1] != NULL && strcmp(args[1],">") == 0 && args[2] != NULL){
						args[1] = NULL;
					}
					//piping
					else if(args[0] != NULL && args[1] != NULL && strcmp(args[1],"|") == 0 && args[2] != NULL){
						//new fork
						pid_t pidc3 = fork();
						if(pidc3 == -1){
							printf("Pipe error!");
							exit(EXIT_FAILURE);
						}
						else if(pidc3 == 0){
							//put the output of first command in pipe
							args[1] = NULL;
							close(p[0]);
							close(STDOUT_FILENO);
				            dup2(p[1],1);
				            execvp(args[0], args);  
				            exit(EXIT_FAILURE);
						}
						else{
							//input pipe to second command
							close(p[1]);
							int status;
							waitpid(pidc3, &status, 0);
							close(0);
				            dup2(p[0],0);
				            execvp(args[2], args+2);
				            exit(EXIT_FAILURE);
						}
					}
					execvp(args[0], args);
					exit(EXIT_FAILURE);
					
				}
				//parent process
				else{
					int status;
					//built-in exit command
					if(strcmp(args[0],"exit") == 0 && args[1] == NULL){
						raise(SIGQUIT);
					}
					//Command piping!!!
					else if(args[0] != NULL && args[1] != NULL && strcmp(args[1],"|") == 0 && args[2] != NULL){
						close(p[1]);
						close(p[0]);
						waitpid(pidc, &status, 0);
			            
					}
					//output direction, wait for the child so we can go back to writing in shell
					else if(args[0] != NULL && args[1] != NULL && strcmp(args[1],">") == 0 && args[2] != NULL){
						waitpid(pidc, &status, 0);
						close(dup_fd);
    					dup(stdout_copy);
					}
					else if(bg == 0){
						pid_t pid = waitpid(pidc, &status, 0);
						//Error
						if (pid == (pid_t) -1) {
				        	    exit(EXIT_FAILURE);
						}
				    }
				    else if(bg == 1){	
				    	//insert child pid in array
						jobs[searchNull()] = pidc;
						sleep(1);
						pid_t pid = waitpid(pidc, &status, WNOHANG);
					}
				    if (WIFSTOPPED(status)) {
	            		printf("Child process have stopped\n");	
					}
				}
			}
		}		
	}
	return 0;
}

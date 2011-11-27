#include<stdio.h>

int main(int argc, char * argv[]){
	FILE * fp = fopen(argv[1], "r");
	unsigned char c;

	while(!feof(fp)){
		fscanf(fp,"%c",&c);
		printf("%d ",c);
	}

	return 0;
}

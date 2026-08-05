// build.c

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <dirent.h>
#include <sys/stat.h>

#ifdef _WIN32
    #define PATH_SEPARATOR '\\'
#else
    #define PATH_SEPARATOR '/'
#endif

#define MAX_COMMAND_SIZE 65536

static char command[MAX_COMMAND_SIZE];

int endsWith(const char *str, const char *suffix)
{
    size_t len1 = strlen(str);
    size_t len2 = strlen(suffix);

    if(len2 > len1)
        return 0;

    return strcmp(str + len1 - len2, suffix) == 0;
}

void scanDirectory(const char *path)
{
    DIR *dir = opendir(path);

    if(dir == NULL){
        printf("Cannot open: %s\n", path);
        return;
   }

    struct dirent *entry;

    while((entry = readdir(dir)) != NULL)
    {
        if(strcmp(entry->d_name, ".") == 0 ||
           strcmp(entry->d_name, "..") == 0)
            continue;

        char fullPath[1024];
        snprintf(fullPath,
                 sizeof(fullPath),
                 "%s%c%s",
                 path,
                 PATH_SEPARATOR,
                 entry->d_name);

        struct stat st;

        if(stat(fullPath, &st) != 0)
            continue;

        if(S_ISDIR(st.st_mode))
        {
            scanDirectory(fullPath);
        }
        else
        {
            if(endsWith(entry->d_name, "-index.ts"))
            {
                printf("Found: %s\n", fullPath);

                strcat(command, "\"");
                strcat(command, fullPath);
                strcat(command, "\" ");
            }
        }
    }

    closedir(dir);
}

int main(void)
{
    strcpy(command, "npx esbuild ");

    scanDirectory("src/main/resources/static/ts");

    strcat(command,
        "--bundle "
        "--minify "
        "--target=esnext "
        "--format=esm "
        "--outbase=src/main/resources/static/ts "
        "--outdir=src/main/resources/static/ts-transpiled");

    printf("\n\n");
    printf("%s\n\n", command);

    int result = system(command);

    if(result == 0)
        printf("Build completed successfully.\n");
    else
        printf("Build failed.\n");

    return result;
}
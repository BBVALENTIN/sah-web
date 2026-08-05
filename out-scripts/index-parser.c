#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <dirent.h>
#include <sys/stat.h>

#ifdef _WIN32
    #include <process.h>
    #define PATH_SEPARATOR '\\'
    #define ESBUILD_BIN ".\\node_modules\\.bin\\esbuild.cmd"
#else
    #include <unistd.h>
    #include <sys/wait.h>
    #define PATH_SEPARATOR '/'
    #define ESBUILD_BIN "./node_modules/.bin/esbuild"
#endif

#define MAX_ENTRIES   512
#define MAX_PATH_LEN  1024

static char *entries[MAX_ENTRIES];
static int entryCount = 0;

int endsWith(const char *str, const char *suffix)
{
    size_t len1 = strlen(str);
    size_t len2 = strlen(suffix);
    if (len2 > len1)
        return 0;
    return strcmp(str + len1 - len2, suffix) == 0;
}

void scanDirectory(const char *path)
{
    DIR *dir = opendir(path);
    if (dir == NULL) {
        fprintf(stderr, "Cannot open: %s\n", path);
        return;
    }

    struct dirent *entry;
    while ((entry = readdir(dir)) != NULL) {
        if (strcmp(entry->d_name, ".") == 0 || strcmp(entry->d_name, "..") == 0)
            continue;

        char fullPath[MAX_PATH_LEN];
        int written = snprintf(fullPath, sizeof(fullPath), "%s%c%s",
                                path, PATH_SEPARATOR, entry->d_name);
        if (written < 0 || (size_t)written >= sizeof(fullPath)) {
            fprintf(stderr, "Path too long, skipping under: %s\n", path);
            continue;
        }

        struct stat st;
        if (stat(fullPath, &st) != 0)
            continue;

        if (S_ISDIR(st.st_mode)) {
            scanDirectory(fullPath);
        } else if (endsWith(entry->d_name, "-index.ts")) {
            if (entryCount >= MAX_ENTRIES) {
                fprintf(stderr, "Too many entry files (limit %d), skipping: %s\n",
                        MAX_ENTRIES, fullPath);
                continue;
            }
            entries[entryCount] = strdup(fullPath);
            if (!entries[entryCount]) {
                fprintf(stderr, "Out of memory\n");
                exit(1);
            }
            printf("Found: %s\n", fullPath);
            entryCount++;
        }
    }
    closedir(dir);
}

int main(void)
{
    scanDirectory("src/main/resources/static/ts");

    if (entryCount == 0) {
        printf("No *-index.ts files found, nothing to build.\n");
        return 0;
    }

    char *argv[MAX_ENTRIES + 16];
    int argc = 0;
    argv[argc++] = ESBUILD_BIN;
    for (int i = 0; i < entryCount; i++)
        argv[argc++] = entries[i];
    argv[argc++] = "--bundle";
    argv[argc++] = "--minify";
    argv[argc++] = "--splitting";
    argv[argc++] = "--target=esnext";
    argv[argc++] = "--format=esm";
    argv[argc++] = "--outbase=src/main/resources/static/ts";
    argv[argc++] = "--outdir=src/main/resources/static/ts-transpiled";
    argv[argc] = NULL;

    printf("\nRunning esbuild with %d entry point(s)...\n\n", entryCount);

    int result;
#ifdef _WIN32
    result = _spawnvp(_P_WAIT, ESBUILD_BIN, (const char * const *)argv);
#else
    pid_t pid = fork();
    if (pid == 0) {
        execvp(ESBUILD_BIN, argv);
        perror("execvp failed");
        _exit(127);
    } else if (pid > 0) {
        int status;
        waitpid(pid, &status, 0);
        result = WIFEXITED(status) ? WEXITSTATUS(status) : -1;
    } else {
        perror("fork failed");
        result = -1;
    }
#endif

    for (int i = 0; i < entryCount; i++)
        free(entries[i]);

    if (result == 0)
        printf("Build completed successfully.\n");
    else
        printf("Build failed (code %d).\n", result);

    return result;
}
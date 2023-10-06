#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <linux/input.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <signal.h>
#include <errno.h>
#include "KeyEventHandler.h"


static const int  BUFFER_SIZE = 12;
static int running = 1;
static int keyboard_fd;
static const char* user_input;
const char *fifoname = "/tmp/keyevent.fifo";

void write_to_pipe(int event_code, int keydown_flag) {
    char input_buffer[BUFFER_SIZE];
    sprintf(input_buffer, "%d", event_code);

    FILE *wr_stream = fopen(fifoname, "w");
    if (wr_stream == (FILE *) NULL) {
        printf("Invalid stream provided\n");
        exit(3);
    }

    size_t elements = fwrite(input_buffer, sizeof(int), strlen(input_buffer), wr_stream);
    if(elements != (size_t) strlen(input_buffer)) {
        printf("Expected %ld, but instead got %d\n", strlen(input_buffer), (int) elements);
        exit(4);
    }
}

void captureKeyEvents() {
    int shift_flag = 0;
    struct input_event event;

    if ((keyboard_fd = open("/dev/input/event1", O_RDONLY)) < 0) {
        fprintf(stderr, "\nUnable to read from the device\n");
        exit(1);
    }

    if ((mkfifo(fifoname,S_IRWXU)) != 0) {
       printf("Unable to create a fifo; errno=%d\n",errno);
    }

    while (running) {
        read(keyboard_fd, &event, sizeof(event));

        if (event.type == EV_KEY && event.value == 1) {
            write_to_pipe(event.code, 1);
        }
        else if (event.type == EV_KEY && event.value == 0) {
                if(event.code == KEY_LEFTSHIFT || event.code == KEY_RIGHTSHIFT) {
                    write_to_pipe(event.code, 0);
                }
        }
    }

    close(keyboard_fd);
    remove(fifoname);
}

JNIEXPORT void JNICALL Java_KeyEventHandler_captureKeyEvents(JNIEnv *env, jobject obj) {
    captureKeyEvents();
}
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

#define UK "UNKNOWN"
#define ESCAPE(key) (key == KEY_ESC)
#define SHIFT(key)  ((key == KEY_LEFTSHIFT) || (key == KEY_RIGHTSHIFT))

static const char *keycodes[] =
{
    "RESERVED", "ESC", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0",
    "-", "=", "BACKSPACE", "TAB", "q", "w", "e", "r", "t", "y", "u", "i",
    "o", "p", "[", "]", "\n", "L_CTRL", "a", "s", "d", "f", "g", "h",
    "j", "k", "l", ";", "'", "`", "L_SHIFT", "\\", "z", "x", "c", "v", "b",
    "n", "m", ",", ".", "/", "R_SHIFT", "*", "L_ALT", " ", "CAPS_LOCK",
    "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10", "NUM_LOCK",
    "SCROLL_LOCK", "NL_7", "NL_8", "NL_9", "-", "NL_4", "NL5",
    "NL_6", "+", "NL_1", "NL_2", "NL_3", "INS", "DEL", UK, UK, UK,
    "F11", "F12", UK, UK,   UK, UK, UK, UK, UK, "R_ENTER", "R_CTRL", "/",
    "PRT_SCR", "R_ALT", UK, "HOME", "UP", "PAGE_UP", "LEFT", "RIGHT", "END",
    "DOWN", "PAGE_DOWN", "INSERT", "DELETE", UK, UK, UK, UK,UK, UK, UK,
    "PAUSE"
};

static const char *shifted_keycodes[] =
{
    "RESERVED", "ESC", "!", "@", "#", "$", "%", "^", "&", "*", "(", ")",
    "_", "+", "BACKSPACE", "TAB", "Q", "W", "E", "R", "T", "Y", "U", "I",
    "O", "P", "{", "}", "\n", "L_CTRL", "A", "S", "D", "F", "G", "H",
    "J", "K", "L", ":", "\"", "~", "L_SHIFT", "|", "Z", "X", "C", "V", "B",
    "N", "M", "<", ">", "?", "R_SHIFT", "*", "L_ALT", " ", "CAPS_LOCK",
    "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10", "NUM_LOCK",
    "SCROLL_LOCK", "HOME", "UP", "PGUP", "-", "LEFT", "NL_5",
    "R_ARROW", "+", "END", "DOWN", "PGDN", "INS", "DEL", UK, UK, UK,
    "F11", "F12", UK, UK, UK, UK, UK, UK, UK, "R_ENTER", "R_CTRL", "/",
    "PRT_SCR", "R_ALT", UK, "HOME", "UP", "PAGE_UP", "LEFT", "RIGHT", "END",
    "DOWN", "PAGE_DOWN", "INSERT", "DELETE", UK, UK, UK, UK, UK, UK, UK,
    "PAUSE"
};

static int running = 1;
static int keyboard_fd;
static const char* user_input;
const char *fifoname = "/tmp/keyevent.fifo";

void write_to_pipe(const char *input) {
    FILE *wr_stream = fopen(fifoname, "w");
    if (wr_stream == (FILE *) NULL) {
        printf("Invalid stream provided\n");
        exit(3);
    }

    size_t elements = fwrite(input, 1, strlen(input), wr_stream);
    if(elements != (size_t) strlen(input)) {
        printf("Expected %ld, but instead got %d\n", strlen(input), (int) elements);
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

        /* If a key from the keyboard is pressed */
        if (event.type == EV_KEY && event.value == 1) {
            write_to_pipe(event.code);
        }
        else {
            /* If a key from the keyboard is released */
            if (event.type == EV_KEY && event.value == 0)
                if (SHIFT(event.code))
                    write_to_pipe(event.code);
        }
    }

    close(keyboard_fd);
    remove(fifoname);
}

JNIEXPORT void JNICALL Java_KeyEventHandler_captureKeyEvents(JNIEnv *env, jobject obj) {
    captureKeyEvents();
}
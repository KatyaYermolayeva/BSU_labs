#include <iostream>
#include <windows.h>
#include "Employee.h"

int main(int argc, char* argv[])
{
    if (argc != 2) {
        printf("Invalid number of parameters");
        return 1;
    }
    char* pipeName = argv[1];

    HANDLE hNamedPipe;
    if (WaitNamedPipeA(pipeName, 0)) {
        hNamedPipe = CreateFileA(pipeName, GENERIC_READ | GENERIC_WRITE,
            FILE_SHARE_READ | FILE_SHARE_WRITE, NULL, OPEN_EXISTING, 0, NULL);
    }
    else {
        printf("Unable to connect to the pipe");
        return 1;
    }

    if (hNamedPipe == INVALID_HANDLE_VALUE) {
        printf("Unable to connect to the pipe");
        return 1;
    }

    char input[10];
    char buf[10];
    int key;
    employee emp;


    while (true) {
        printf("What to do?(q - quit, m - modify record, otherwise - read record):\n");
        std::cin.getline(input, _countof(input));
        if (!WriteFile(hNamedPipe, input, sizeof(input), NULL, NULL))
        {
            printf("Failed to send request to the Server");
            break;
        }
        if (input[0] == 'q' && input[1] == 0) {
            break;
        }
        key = 0;
        printf("Enter record key(employee's ID):\n");
        while (key == 0) {
            std::cin.getline(buf, _countof(buf));
            key = atoi(buf);
        }
        if (!WriteFile(hNamedPipe, &key, sizeof(key), NULL, NULL))
        {
            printf("Failed to send request to the Server");
            break;
        }
        if (!ReadFile(hNamedPipe, &emp, sizeof(emp), NULL, NULL))
        {
            printf("Failed to receive Servers's response");
            break;
        }
        if (emp.num == -1) {
            printf("Record not found\n");
            continue;
        }

        printf("%d %s %lf\n", emp.num, emp.name, emp.hours);
        if (input[0] == 'm' && input[1] == 0) {
            printf("Enter new values:\n");
            while (true) {
                printf("Enter employee's ID:\n");
                std::cin.getline(buf, _countof(buf));
                emp.num = atoi(buf);
                if (emp.num == 0) {
                    printf("Invalid input, try again\n");
                    continue;
                }
                printf("Enter employee's name:\n");
                std::cin.getline(emp.name, _countof(emp.name));
                printf("Enter employee's work hours:\n");
                std::cin.getline(buf, _countof(buf));
                emp.hours = atof(buf);
                if (emp.hours == 0) {
                    printf("Invalid input, try again\n");
                    continue;
                }
                break;
            }
            if (!WriteFile(hNamedPipe, &emp, sizeof(emp), NULL, NULL))
            {
                printf("Failed to send request to the Server");
                break;
            }
        }
        
        printf("Enter anything to exit record: ");
        std::cin.getline(input, _countof(input));
        if (!WriteFile(hNamedPipe, &input, sizeof(input), NULL, NULL))
        {
            printf("Failed to send request to the Server");
            break;
        }
    }

    CloseHandle(hNamedPipe);
    return 0;
}
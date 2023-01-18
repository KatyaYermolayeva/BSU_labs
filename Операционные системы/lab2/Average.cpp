#include <iostream>
#include <windows.h>
#include "CustomArray.h"

DWORD WINAPI average(LPVOID pArray) {
    CustomArray* arr = (CustomArray*)pArray;
    int sum = 0;

    for (int i = 0; i < arr->size; i++) {
        sum += arr->array[i];
        Sleep(500);
    }

    arr->average = sum / arr->size;

    printf("Average value = %d\n", arr->average);

    return 0;
}
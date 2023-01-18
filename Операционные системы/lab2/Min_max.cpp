#include <iostream>
#include <windows.h>
#include "CustomArray.h"

DWORD WINAPI min_max(LPVOID pArray) {
    CustomArray* arr = (CustomArray*)pArray;

    for (int i = 0; i < arr->size; i++) {
        if (arr->array[i] < arr->array[arr->minIndex]) {
            arr->minIndex = i;
        }
        else if (arr->array[i] > arr->array[arr->maxIndex]) {
            arr->maxIndex = i;
        }
        Sleep(700);
    }

    printf("Min value = %d, max value = %d\n", arr->array[arr->minIndex], arr->array[arr->maxIndex]);

    return 0;
}
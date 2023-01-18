#include <iostream>
#include <windows.h>
#include "CustomArray.h"

DWORD WINAPI min_max(LPVOID a);
DWORD WINAPI average(LPVOID a);

int main()
{
    HANDLE	hAverage, hMin_max;
    DWORD	IDAverage, IDMin_max;

    CustomArray arr{NULL, 0, 0, 0, 0};

    printf("Enter array's size (>0): ");
    scanf_s("%d", &arr.size);

    arr.array = new int[arr.size];

    printf("Enter %d integers:\n", arr.size);
    for (int i = 0; i < arr.size; i++) {
        scanf_s("%d", &arr.array[i]);
    }

    printf("Array created!\n");

    hAverage = CreateThread(NULL, 0, average, &arr, 0, &IDAverage);
    if (hAverage == NULL) {
        printf("Failed to create average thread.");
        getc(stdin);
        return 1;
    }
    hMin_max = CreateThread(NULL, 0, min_max, &arr, 0, &IDMin_max);
    if (hMin_max == NULL) {
        printf("Failed to create min_max thread.");
        getc(stdin);
        return 1;
    }

    WaitForSingleObject(hAverage, INFINITE);
    WaitForSingleObject(hMin_max, INFINITE);
    HANDLE handles[] = { hAverage, hMin_max };

    WaitForMultipleObjects(2, handles, TRUE, INFINITE);

    CloseHandle(hAverage);
    CloseHandle(hMin_max);

    arr.array[arr.minIndex] = arr.average;
    arr.array[arr.maxIndex] = arr.average;

    printf("Result array: \n");
    for (int i = 0; i < arr.size; i++) {
        printf("%d ", arr.array[i]);
    }

    delete[] arr.array;

    getc(stdin);
    getc(stdin);

    return 0;
}

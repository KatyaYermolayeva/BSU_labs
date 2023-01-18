#include <iostream>
#include <windows.h>

HANDLE* hMarkers;
DWORD* IDMarkers;
int* arr;
int arraySize, nMarkers;

CRITICAL_SECTION arrayCS;
HANDLE hStartEvent;
HANDLE hContinueEvent;
HANDLE* hFinishEvents;
HANDLE* hUnableToContinueEvents;
int* isFinished;

DWORD WINAPI marker(void* number);
int main()
{
	printf("Enter array's size: ");
	scanf_s("%d", &arraySize);
	arr = new int[arraySize] {0};

	printf("Enter number of markers: ");
	scanf_s("%d", &nMarkers);
	hMarkers = new HANDLE[nMarkers];
	IDMarkers = new DWORD[nMarkers];
	hFinishEvents = new HANDLE[nMarkers];
	hUnableToContinueEvents = new HANDLE[nMarkers];
	isFinished = new int[nMarkers];

	hStartEvent = CreateEvent(NULL, TRUE, FALSE, NULL);
	if (hStartEvent == NULL) {
		return GetLastError();
	}
	hContinueEvent = CreateEvent(NULL, TRUE, FALSE, NULL);
	if (hContinueEvent == NULL) {
		return GetLastError();
	}

	for (int i = 0; i < nMarkers; i++) {
		hMarkers[i] = CreateThread(NULL, 0, marker, (void*)(i + 1), 0, &IDMarkers[i]);
		if (hMarkers[i] == NULL) {
			return GetLastError();
		}
		hFinishEvents[i] = CreateEvent(NULL, FALSE, FALSE, NULL);
		if (hFinishEvents[i] == NULL) {
			return GetLastError();
		}
		hUnableToContinueEvents[i] = CreateEvent(NULL, FALSE, FALSE, NULL);
		if (hUnableToContinueEvents[i] == NULL) {
			return GetLastError();
		}
		isFinished[i] = 0;
	}

	InitializeCriticalSection(&arrayCS);
	SetEvent(hStartEvent);
	for (int i = 0; i < nMarkers; i++) {
		WaitForMultipleObjects(nMarkers, hUnableToContinueEvents, TRUE, INFINITE);

		printf("\nArray:\n");
		for (int i = 0; i < arraySize; i++) {
			printf("%d ", arr[i]);
		}
		printf("\nEnter number of marker to stop (1 to %d): ", nMarkers);
		int k;

		while (true) {
			scanf_s("%d", &k);
			if (k > nMarkers || k < 1) {
				printf("\nInvalid thread number. Choose another thread: ");
			}
			else if (isFinished[k - 1] != 0) {
				printf("\nThis thread has already finished. Choose another thread: ");
			}
			else break;
		}
		k--;
		isFinished[k] = 1;
		SetEvent(hFinishEvents[k]);
		WaitForSingleObject(hMarkers[k], INFINITE);
		CloseHandle(hUnableToContinueEvents[k]);
		hUnableToContinueEvents[k] = CreateEvent(NULL, TRUE, TRUE, NULL);
		if (hUnableToContinueEvents[k] == NULL) {
			return GetLastError();
		}

		printf("\nArray:\n");
		for (int i = 0; i < arraySize; i++) {
			printf("%d ", arr[i]);
		}
		printf("\n");
		SetEvent(hContinueEvent);
	}

	DeleteCriticalSection(&arrayCS);
	CloseHandle(hContinueEvent);
	CloseHandle(hStartEvent);
	for (int i = 0; i < nMarkers; i++) {
		CloseHandle(hMarkers[i]);
		CloseHandle(hUnableToContinueEvents[i]);
		CloseHandle(hFinishEvents[i]);
	}
	delete[] hMarkers, IDMarkers, arr, hUnableToContinueEvents, hFinishEvents, isFinished;
	printf("\nDone!\n");
	return 0;
}

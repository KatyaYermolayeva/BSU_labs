#include <iostream>
#include <windows.h>

DWORD WINAPI marker(void* n)
{
	extern CRITICAL_SECTION arrayCS;
	extern HANDLE hStartEvent;
	extern HANDLE hContinueEvent;

	extern HANDLE* hFinishEvents;
	extern HANDLE* hUnableToContinueEvents;

	extern int arraySize;
	extern int* arr;

	int number = (int)n;

	HANDLE hContinueOrFinishEvent[] = { hContinueEvent, hFinishEvents[number - 1] };

	int nMarked = 0;

	WaitForSingleObject(hStartEvent, INFINITE);

	srand(number);
	while (true) {
		int i = rand();
		i %= arraySize;

		EnterCriticalSection(&arrayCS);
		if (arr[i] == 0) {
			Sleep(5);
			arr[i] = number;
			LeaveCriticalSection(&arrayCS);
			nMarked++;
			Sleep(5);
		}
		else {
			LeaveCriticalSection(&arrayCS);

			printf("Thread number - %d, number of marked elements - %d, unable to mark element at index %d\n",
				number, nMarked, i);
			ResetEvent(hContinueEvent);
			SetEvent(hUnableToContinueEvents[number - 1]);
			DWORD waitResult = WaitForMultipleObjects(2, hContinueOrFinishEvent, FALSE, INFINITE);
			if (waitResult - WAIT_OBJECT_0 == 1) {
				break;
			}
		}
	}
	for (int i = 0; i < arraySize; i++) {
		if (arr[i] == number) {
			arr[i] = 0;
		}
	}
	return 0;
}

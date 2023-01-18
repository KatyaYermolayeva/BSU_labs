#include <iostream>;
#include <windows.h>;

extern int head;
extern int tail;
extern char* queue[20];
extern int messageLength;
extern int nRecords;

DWORD WINAPI Sender(void* a) {
	char eventName[30];
	char fullSemaphoreName[] = "fullSemaphore";
	char emptySemaphoreName[] = "emptySemaphore";
	char arrayMutexName[] = "arrayMutex";
	_snprintf_s(eventName, _countof(eventName), "%s%d", "startedEvent", (int)a);


	HANDLE hStartedEvent;
	HANDLE hFullSemaphore;
	HANDLE hEmptySemaphore;
	HANDLE hArrayMutex;

	hStartedEvent = OpenEventA(EVENT_ALL_ACCESS, FALSE, eventName);
	if (hStartedEvent == NULL) {
		return GetLastError();
	}
	hFullSemaphore = OpenSemaphoreA(SEMAPHORE_ALL_ACCESS, FALSE, fullSemaphoreName);
	if (hFullSemaphore == NULL) {
		return GetLastError();
	}
	hEmptySemaphore = OpenSemaphoreA(SEMAPHORE_ALL_ACCESS, FALSE, emptySemaphoreName);
	if (hEmptySemaphore == NULL) {
		return GetLastError();
	}
	hArrayMutex = OpenMutexA(MUTEX_ALL_ACCESS, FALSE, arrayMutexName);
	if (hArrayMutex == NULL) {
		return GetLastError();
	}

	SetEvent(hStartedEvent);

	char input;
	char* message = new char[messageLength];
	while (true) {
		printf("[thread n. %d]: What to do? (q - quit, otherwise - write into file): ", (int)a);
		input = getc(stdin);
		if (input == 'q') {
			break;
		}
		printf("Enter message(20 characters max):\n");
		gets_s(message, messageLength);
		WaitForSingleObject(hEmptySemaphore, INFINITE);
		WaitForSingleObject(hArrayMutex, INFINITE);
		strcpy_s(queue[tail], messageLength, message);
		tail = (tail + 1) % nRecords;
		ReleaseMutex(hArrayMutex);
		ReleaseSemaphore(hFullSemaphore, 1, NULL);
	}

	delete[] message;
	return 0;
}
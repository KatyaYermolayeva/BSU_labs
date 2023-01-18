#include <iostream>;
#include <windows.h>;

int main() {
	STARTUPINFOA* siSenders;
	PROCESS_INFORMATION* piSenders;

	HANDLE* hStartedEvents;
	HANDLE hFullSemaphore;
	HANDLE hEmptySemaphore;
	HANDLE hFileMutex;

	char eventName[30];
	char fileName[30];
	char senderArguments[200];
	char fullSemaphoreName[] = "fullSemaphore";
	char emptySemaphoreName[] = "emptySemaphore";
	char fileMutexName[] = "arrayMutex";

	int nRecords, head = 0, tail = 0;

	printf("Enter binary file name: ");
	std::cin.getline(fileName, _countof(fileName));

	printf("Enter number of records: ");
	scanf_s("%d", &nRecords);

	FILE* bFile;
	bFile = _fsopen(fileName, "w+b", _SH_DENYNO);
	if (bFile == NULL) {
		printf("Failed to create %s\n", fileName);
		return 1;
	}
	fwrite(&nRecords, sizeof(nRecords), 1, bFile);
	fwrite(&tail, sizeof(tail), 1, bFile);

	fseek(bFile, 0, SEEK_SET);

	hFullSemaphore = CreateSemaphoreA(NULL, 0, nRecords, fullSemaphoreName);
	if (hFullSemaphore == NULL) {
		return GetLastError();
	}
	hEmptySemaphore = CreateSemaphoreA(NULL, nRecords, nRecords, emptySemaphoreName);
	if (hEmptySemaphore == NULL) {
		return GetLastError();
	}
	hFileMutex = CreateMutexA(NULL, FALSE, fileMutexName);
	if (hFileMutex == NULL) {
		return GetLastError();
	}

	int nSenders;
	printf("Enter number of senders: ");
	scanf_s("%d", &nSenders);

	siSenders = new STARTUPINFOA[nSenders];
	piSenders = new PROCESS_INFORMATION[nSenders];
	hStartedEvents = new HANDLE[nSenders];

	for (int i = 0; i < nSenders; i++) {
		_snprintf_s(eventName, _countof(eventName), "%s%d", "startedEvent", i);
		hStartedEvents[i] = CreateEventA(NULL, FALSE, FALSE, eventName);
		if (hStartedEvents[i] == NULL) {
			return GetLastError();
		}

		ZeroMemory(&siSenders[i], sizeof(siSenders[i]));
		siSenders[i].cb = sizeof(siSenders[i]);
		ZeroMemory(&piSenders[i], sizeof(piSenders[i]));

		_snprintf_s(senderArguments, _countof(senderArguments), "%s %s %s %s %s %s",
			"Sender.exe", fullSemaphoreName, emptySemaphoreName, fileMutexName, eventName, fileName);
		if (!CreateProcessA(NULL, senderArguments, NULL, NULL, FALSE,
			CREATE_NEW_CONSOLE, NULL, NULL, &siSenders[i], &piSenders[i]))
		{
			printf("Not able to start Sender");
			return 1;
		};
	}

	WaitForMultipleObjects(nSenders, hStartedEvents, TRUE, INFINITE);

	char input[5];
	char message[20];
	std::cin.getline(input, 5);
	int count = 0;
	while(count < 20) {
		//printf("\nWhat to do? (q - quit, otherwise - read from file): ");
		//std::cin.getline(input, 5);
		//if (!strcmp(input, "q")) {
		//	break;
		//}

		WaitForSingleObject(hFullSemaphore, INFINITE);
		WaitForSingleObject(hFileMutex, INFINITE);
		fseek(bFile, sizeof(int) * 2 + head * sizeof(message), SEEK_SET);
		fread(message, sizeof(message), 1, bFile);
		Sleep(1000);
		ReleaseMutex(hFileMutex);
		ReleaseSemaphore(hEmptySemaphore, 1, NULL);
		head = (head + 1) % nRecords;
		printf("Message read from file:\n");
		printf(message);
		printf("\n");
		count++;
	}

	for (int i = 0; i < nSenders; i++) {
		CloseHandle(piSenders[i].hThread);
		CloseHandle(piSenders[i].hProcess);
	}

	for (int i = 0; i < nSenders; i++) {
		CloseHandle(hStartedEvents[i]);
		CloseHandle(hFullSemaphore);
		CloseHandle(hEmptySemaphore);
		CloseHandle(hFileMutex);
	}

	fclose(bFile);

	delete[] siSenders, hStartedEvents, piSenders;

	return 0;
}
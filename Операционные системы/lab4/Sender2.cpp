#include <iostream>;
#include <windows.h>;

int main(int argc, char* argv[]) {
	if (argc != 6) {
		printf("Invalid number of parameters");
		return 1;
	}

	char* fullSemaphoreName = argv[1];
	char* emptySemaphoreName = argv[2];
	char* fileMutexName = argv[3];
	char* eventName = argv[4];
	char* fileName = argv[5];

	HANDLE hStartedEvent;
	HANDLE hFullSemaphore;
	HANDLE hEmptySemaphore;
	HANDLE hFileMutex;

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
	hFileMutex = OpenMutexA(MUTEX_ALL_ACCESS, FALSE, fileMutexName);
	if (hFileMutex == NULL) {
		return GetLastError();
	}

	SetEvent(hStartedEvent);

	FILE* bFile;
	bFile = _fsopen(fileName, "r+b", _SH_DENYNO);
	if (bFile == NULL) {
		printf("Failed to open %s\n", fileName);
		return 1;
	}

	int nRecords, tail;
	fseek(bFile, 0, SEEK_SET);
	fread(&nRecords, sizeof(nRecords), 1, bFile);
	
	char input[5];
	char message[20];
	int count = 0;
	while (count < 20) {
		/*printf("What to do? (q - quit, otherwise - write into file): ");
		std::cin.getline(input, 5);
		if (!strcmp(input, "q")) {
			break;
		}*/
		
		WaitForSingleObject(hEmptySemaphore, INFINITE);
		/*printf("Enter message(20 characters max):\n");
		std::cin.getline(message, 20);*/
		_snprintf_s(message, _countof(message), "%s %d", eventName, count);
		count++;
		WaitForSingleObject(hFileMutex, INFINITE);
		Sleep(200);
		fseek(bFile, sizeof(int), SEEK_SET);
		fread(&tail, sizeof(tail), 1, bFile);
		fseek(bFile, sizeof(int) * 2 + tail * sizeof(message), SEEK_SET);
		fwrite(message, sizeof(message), 1, bFile);
		tail = (tail + 1) % nRecords;
		fseek(bFile, sizeof(int), SEEK_SET);
		fwrite(&tail, sizeof(tail), 1, bFile);
		fseek(bFile, 0, SEEK_SET);
		ReleaseMutex(hFileMutex);
		ReleaseSemaphore(hFullSemaphore, 1, NULL);
	}

	fclose(bFile);

	return 0;
}
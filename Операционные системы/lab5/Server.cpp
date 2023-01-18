#include <iostream>
#include "Employee.h"
#include <windows.h>

struct ThreadParams {
	int* readersCount;
	HANDLE* write;
	CRITICAL_SECTION* readersCountCSs;
	char bFileName[30];
	int nClients;
	int nRecords;
};

DWORD WINAPI clientReadWrite(void* params) {
	STARTUPINFOA siClient;
	PROCESS_INFORMATION piClient;
	ThreadParams* threadParams = (ThreadParams*)params;
	HANDLE hNamedPipe;

	char clientArguments[50];
	char pipeName[] = "\\\\.\\pipe\\myPipe";
	_snprintf_s(clientArguments, _countof(clientArguments), "%s %s", "Client.exe", pipeName);
	hNamedPipe = CreateNamedPipeA(pipeName, PIPE_ACCESS_DUPLEX,
		PIPE_TYPE_MESSAGE | PIPE_WAIT, threadParams->nClients, 0, 0, INFINITE, NULL);
	if (hNamedPipe == INVALID_HANDLE_VALUE) {
		printf("Not able to create named pipe\n");
		printf("%d\n", GetLastError());
		return 1;
	}

	ZeroMemory(&siClient, sizeof(siClient));
	siClient.cb = sizeof(siClient);
	ZeroMemory(&piClient, sizeof(piClient));
	if (!CreateProcessA(NULL, clientArguments, NULL, NULL, FALSE,
		CREATE_NEW_CONSOLE, NULL, NULL, &siClient, &piClient))
	{
		printf("Not able to start Client\n");
		CloseHandle(hNamedPipe);
		return 1;
	};

	if (!ConnectNamedPipe(hNamedPipe, NULL)) {
		printf("Not able to connect to the Client\n");
	}
	else {
		FILE* bFile;
		bFile = _fsopen(threadParams->bFileName, "r+b", _SH_DENYNO);
		if (bFile == NULL) {
			printf("Failed to open %s\n", threadParams->bFileName);
			return 1;
		}

		char input[10];
		int key;
		int index;
		bool isPresent;
		employee emp = employee();
		while (true) {
			if (!ReadFile(hNamedPipe, input, sizeof(input), NULL, NULL))
			{
				printf("Failed to receive Client's message\n");
				break;
			}
			if (input[0] == 'q' && input[1] == 0) {
				break;
			}
			if (!ReadFile(hNamedPipe, &key, sizeof(key), NULL, NULL))
			{
				printf("Failed to receive Client's message\n");
				break;
			}

			fseek(bFile, 0, SEEK_SET);
			index = 0;
			isPresent = false;
			while (fread(&emp, sizeof(emp), 1, bFile)) {
				if (emp.num == key) {
					isPresent = true;
					break;
				}
				index++;
			}
			if (!isPresent) {
				emp.num = -1;
				if (!WriteFile(hNamedPipe, &emp, sizeof(emp), NULL, NULL))
				{
					printf("Failed to send record to a Client\n");
				}
				continue;
			}
			if (input[0] == 'm' && input[1] == 0) {
				WaitForSingleObject(threadParams->write[index], INFINITE);
				if (!WriteFile(hNamedPipe, &emp, sizeof(emp), NULL, NULL))
				{
					printf("Failed to send record to a Client\n");
					ReleaseSemaphore(threadParams->write[index], 1, NULL);
					break;
				}
				if (!ReadFile(hNamedPipe, &emp, sizeof(emp), NULL, NULL))
				{
					printf("Failed to receive Client's message\n");
					ReleaseSemaphore(threadParams->write[index], 1, NULL);
					break;
				}
				fseek(bFile, index * sizeof(emp), SEEK_SET);
				fwrite(&emp, sizeof(emp), 1, bFile);
				fseek(bFile, 0, SEEK_SET);
				if (!ReadFile(hNamedPipe, input, sizeof(input), NULL, NULL))
				{
					printf("Failed to receive Client's message\n");
					ReleaseSemaphore(threadParams->write[index], 1, NULL);
					break;
				}
				ReleaseSemaphore(threadParams->write[index], 1, NULL);
			}
			else {
				EnterCriticalSection(&threadParams->readersCountCSs[index]);
				threadParams->readersCount[index]++;
				if (threadParams->readersCount[index] == 1) {
					WaitForSingleObject(threadParams->write[index], INFINITE);
				}
				LeaveCriticalSection(&threadParams->readersCountCSs[index]);
				fseek(bFile, index * sizeof(emp), SEEK_SET);
				fread(&emp, sizeof(emp), 1, bFile);
				if (!WriteFile(hNamedPipe, &emp, sizeof(emp), NULL, NULL))
				{
					printf("Failed to send record to a Client\n");
					ReleaseSemaphore(threadParams->write[index], 1, NULL);
					break;
				}
				if (!ReadFile(hNamedPipe, input, sizeof(input), NULL, NULL))
				{
					printf("Failed to receive Client's message\n");
					ReleaseSemaphore(threadParams->write[index], 1, NULL);
					break;
				}
				EnterCriticalSection(&threadParams->readersCountCSs[index]);
				threadParams->readersCount[index]--;
				if (threadParams->readersCount[index] == 0) {
					ReleaseSemaphore(threadParams->write[index], 1, NULL);
				}
				LeaveCriticalSection(&threadParams->readersCountCSs[index]);
			}
		}
		fclose(bFile);
	}

	DisconnectNamedPipe(hNamedPipe);
	CloseHandle(hNamedPipe);
	CloseHandle(piClient.hThread);
	CloseHandle(piClient.hProcess);

	return 0;
}

int main()
{
	HANDLE* hReadWrite;
	int nRecords = 0;
	char input[10];
	employee emp;
	int check;
	ThreadParams threadParams = ThreadParams();

	printf("Enter the name of a binary file:\n");
	std::cin.getline(threadParams.bFileName, _countof(threadParams.bFileName));

	FILE* bFile;
	bFile = _fsopen(threadParams.bFileName, "w+b", _SH_DENYNO);
	if (bFile == NULL) {
		printf("Failed to create %s\n", threadParams.bFileName);
		return 1;
	}

	printf("Enter employee records:\n");
	while (true)
	{
		printf("What to do? (q - finish entering record, otherwise - enter a new record): ");
		std::cin.getline(input, _countof(input));	
		if (input[0] == 'q' && input[1] == 0) {
			break;
		}
		printf("Enter employee's ID:\n");
		std::cin.getline(input, _countof(input));
		emp.num = atoi(input);
		if (emp.num == 0) {
			printf("Invalid input, try again\n");
			continue;
		}
		printf("Enter employee's name:\n");
		std::cin.getline(emp.name, _countof(emp.name));
		printf("Enter employee's work hours:\n");
		std::cin.getline(input, _countof(input));
		emp.hours = atof(input);
		if (emp.hours == 0) {
			printf("Invalid input, try again\n");
			continue;
		}
		fwrite(&emp, sizeof(emp), 1, bFile);
		nRecords++;
	}

	threadParams.readersCount = new int[nRecords];
	threadParams.readersCountCSs = new CRITICAL_SECTION[nRecords];
	threadParams.write = new HANDLE[nRecords];

	for (int i = 0; i < nRecords; i++)
	{
		threadParams.readersCount[i] = 0;
		threadParams.write[i] = CreateSemaphoreA(NULL, 1, 1, NULL);
		InitializeCriticalSection(&threadParams.readersCountCSs[i]);
	}

	fseek(bFile, 0, SEEK_SET);
	printf("%s:\n", threadParams.bFileName);
	while (fread(&emp, sizeof(employee), 1, bFile)) {
		printf("%d %s %lf\n", emp.num, emp.name, emp.hours);
	}

	printf("Enter the number of Clients:\n");
	scanf_s("%d", &threadParams.nClients);

	hReadWrite = new HANDLE[threadParams.nClients];
	for (int i = 0; i < threadParams.nClients; i++)
	{
		hReadWrite[i] = CreateThread(NULL, 0, clientReadWrite, &threadParams, 0, NULL);
		if (hReadWrite[i] == NULL) {
			printf("Failed to create thread.\n");
			return 1;
		}
	}

	WaitForMultipleObjects(threadParams.nClients, hReadWrite, TRUE, INFINITE);

	fseek(bFile, 0, SEEK_SET);
	printf("Modified %s:\n", threadParams.bFileName);
	while (fread(&emp, sizeof(employee), 1, bFile)) {
		printf("%d %s %lf\n", emp.num, emp.name, emp.hours);
	}

	fclose(bFile);

	for (int i = 0; i < threadParams.nClients; i++) {
		CloseHandle(hReadWrite[i]);	
	}
	for (int i = 0; i < nRecords; i++) {
		CloseHandle(threadParams.write[i]);
		DeleteCriticalSection(&threadParams.readersCountCSs[i]);
	}

	delete[] hReadWrite, threadParams.write, threadParams.readersCount;

	printf("Press any button to finish\n");
	getc(stdin);
	getc(stdin);

	return 0;
}

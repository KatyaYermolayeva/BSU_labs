#include <iostream>
#include <windows.h>
#include <conio.h>

using namespace std;

struct employee {
	int num;
	char name[10];
	double hours;
};

int main()
{
	STARTUPINFO siCreator, siReporter;
	PROCESS_INFORMATION piCreator, piReporter;
	DWORD exitCode;

	ZeroMemory(&siCreator, sizeof(siCreator));
	ZeroMemory(&siReporter, sizeof(siReporter));
	siCreator.cb = sizeof(siCreator);
	siReporter.cb = sizeof(siReporter);
	ZeroMemory(&piCreator, sizeof(piCreator));
	ZeroMemory(&piReporter, sizeof(piReporter));

	wchar_t  creatorArguments[200], reporterArguments[200];
	wchar_t  bFilename[50], reportFilename[50];
	int nRecords;
	double payment;

	printf("Enter the name of a binary file:\n");
	scanf_s("%ls", bFilename, _countof(bFilename));
	printf("Enter the number of records in it:\n");
	scanf_s("%d", &nRecords);
	_snwprintf_s(creatorArguments, _countof(creatorArguments), L"%s %s %d", L"Creator.exe", bFilename, nRecords);

	if(!CreateProcess(NULL, creatorArguments, NULL, NULL, FALSE,
		CREATE_NEW_CONSOLE, NULL, NULL, &siCreator, &piCreator)) 
	{
		printf("Not able to start Creator");
		getc(stdin);
		return 1;
	};

	printf("The Creator process is created!\n");

	WaitForSingleObject(piCreator.hProcess, INFINITE);
	GetExitCodeProcess(piCreator.hProcess, &exitCode);
	CloseHandle(piCreator.hThread);
	CloseHandle(piCreator.hProcess);

	if (exitCode != 0) {
		printf("Creator failed.\n");
		getc(stdin);
		return 1;
	}

	FILE* f;
	_wfopen_s(&f, bFilename, L"rb");
	if (f == NULL) {
		printf("Failed to open %ls\n", bFilename);
		getc(stdin);
		return 1;
	}

	employee emp;
	while (fread(&emp, sizeof(employee), 1, f)) {
		printf("%d %s %f\n", emp.num, emp.name, emp.hours);
	}
	fclose(f);

	printf("Enter the name of a report file:\n");
	scanf_s("%ls", reportFilename, _countof(reportFilename));
	printf("Enter hourly payment:\n");
	scanf_s("%lf", &payment);
	_snwprintf_s(reporterArguments, _countof(reporterArguments), L"%s %s %s %lf", L"Reporter.exe", bFilename, reportFilename, payment);

	if (!CreateProcess(NULL, reporterArguments, NULL, NULL, FALSE,
		CREATE_NEW_CONSOLE, NULL, NULL, &siReporter, &piReporter)) 
	{
		printf("Not able to start Reporter");
		getc(stdin);
		return 1;
	};

	printf("The Reporter process is created!\n");

	WaitForSingleObject(piReporter.hProcess, INFINITE);
	GetExitCodeProcess(piReporter.hProcess, &exitCode);
	CloseHandle(piReporter.hThread);
	CloseHandle(piReporter.hProcess);

	if (exitCode != 0) {
		printf("Reporter failed.\n");
		getc(stdin);
		return 1;
	}

	_wfopen_s(&f, reportFilename, L"rt");

	if (f == NULL) {
		printf("Failed to open %ls\n", reportFilename);
		getc(stdin);
		return 1;
	}

	char s[200];
	while (fgets(s, _countof(s), f)) {
		printf("%s", s);
	}
	fclose(f);

	getc(stdin);
	getc(stdin);

	return 0;
}
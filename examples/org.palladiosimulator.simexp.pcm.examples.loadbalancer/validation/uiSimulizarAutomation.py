import sys
import re
import random
import subprocess
import time
import os
import tempfile

max_upperThreshold = 2.0
max_lowerThreshold = 2.0
max_scaleFactor = 1.0
rounding_resolution = 5
outsource_activated = False
generateRandomValues = False

def runSimulizarAutomation(args):
    numOfRuns = args[0]
    pathQvtoFile = args[1]
    for i in range(int(numOfRuns)):
        if (generateRandomValues & (i > 0)):
            updateQvtoFile(pathQvtoFile)
        runSimulizar()
    print("done")


def updateQvtoFile(pathQvtoFile):
    with open(pathQvtoFile, 'r') as file:
        lines = file.readlines()

    with open(pathQvtoFile, 'w') as readFile:
        for line in lines:
            if re.search(r"property upperThreshold : Real = [0-9]*\.?[0-9]+;", line):
                randomNumber = round(random.uniform(0.0, max_upperThreshold), rounding_resolution)
                line = re.sub(r"[0-9]*\.?[0-9]+", str(randomNumber), line)
            if re.search(r"property lowerThreshold : Real = [0-9]*\.?[0-9]+;", line):
                randomNumber = round(random.uniform(0.0, max_lowerThreshold), rounding_resolution)
                line = re.sub(r"[0-9]*\.?[0-9]+", str(randomNumber), line)
            if outsource_activated:
                if re.search(r"property outsourceFactor : Real = [0-9]*\.?[0-9]+;", line):
                    randomNumber = round(random.uniform(0.0, max_scaleFactor), rounding_resolution)
                    line = re.sub(r"[0-9]*\.?[0-9]+", str(randomNumber), line)
            readFile.write(line)


def runSimulizar():
    while True:
        try:
            subprocess.run(["autohotkey", "HelloWorld.ahk"], cwd="C:/Users/nr595/Documents/AutoHotkey", check=True)
        except FileNotFoundError:
            print("Error: Befehl nicht gefunden")
        except subprocess.CalledProcessError as e:
            print(f"Befehl fehlgeschlagen: {e}")

        #time.sleep(0.5)
        while not os.path.exists("C:\\dev\\workspaces\\runtime-SimExp-ma_bruening-ea\\logSimulizar\\logSimulizar.log"):
            time.sleep(80)
        with open("C:\\dev\\workspaces\\runtime-SimExp-ma_bruening-ea\\logSimulizar\\logSimulizar.log", 'r') as file:
            lines = file.readlines()
            for line in lines:
                if re.search(r".*INFO : Workflow engine completed task", line):
                    print("Runtime finished successfully")
                    return
        print("Simulizar Run failed")



if __name__ == '__main__':
    runSimulizarAutomation(sys.argv[1:])



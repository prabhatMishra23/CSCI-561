import sys
import os

def readoutput(filename):
    answer=[]
    with open(filename,'r') as file:
        for line in file:
            answer.append(line.rstrip())

    if answer[-1]=='':
        return answer[:-1]

    return answer

def ansCheck(myoutput,stdoutput):
    truth=readoutput(myoutput)
    stdans=readoutput(stdoutput)
    if len(truth)!=len(stdans):
        return ['D',truth,stdans]
    diff=[i+1 for i in range(len(truth)) if (truth[i]=='FAIL' and stdans[i]!='FAIL')or(truth[i]!='FAIL' and stdans[i]=='FAIL') ]
    return diff

if _name_ == "_main_":
    #compare two output
    #print PASS or give wrong line number
    result=ansCheck(sys.argv[1],sys.argv[2])
    if not result:
        print('PASS')
    elif result[0]=='D':
        print('More or Less lines in the your answer')
        print(result[1])
        print(result[2])
    else:
        print('Wrong line number:')
        for i in range(len(result)):
            print(result[i])
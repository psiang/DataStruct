import csv
import random

fp = open('./DouBan.csv', 'r',encoding='gbk')
fp2 = open('./BookList.csv', 'w',newline='',encoding='UTF-8')
writer = csv.writer(fp2)
count = 0

for line in csv.reader(fp):
    if count == 0:
        count = 1
        continue
    print(line)
    writer.writerow((line[0].replace(",","，"), line[1].split(',')[0].replace("?","·"), line[2], 0))


fp.close()
fp2.close()
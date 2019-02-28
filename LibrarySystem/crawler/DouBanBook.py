import time
import requests
import re
import csv
import random
from lxml import etree

headers = {
    'User-Agent':'Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36'
}

fp = open('./DouBan.csv', 'a', newline='',encoding='utf-8')
writer = csv.writer(fp)

def get_book_info(url):
    html = requests.get(url, headers=headers)
    selector = etree.HTML(html.text)
    try:
        infos= selector.xpath('//li[@class="subject-item"]')
        for info in infos:
            name = info.xpath('div[2]/h2/a/@title')[0]
            author = info.xpath('div[2]/div[1]/text()')[0].split('/')[0].split('、')[0].strip()
            author = re.sub(u"\\(.*?\\)|\\（.*?）|\\{.*?}|\\[.*?]|\\【.*?】", "", author)
            author = re.sub(u".*? 著", "", author)
            author = author.strip()
            #print(name, author)
            writer.writerow((name, author, round(random.uniform(1, 51)), 0))
    except IndexError:
        pass

if __name__ == '__main__':
    urls = ['https://book.douban.com/tag/摄影?start={}&type=T'.format(number) for number in range(0, 1000, 20)]
    page = 1
    for url in urls:
        get_book_info(url)
        print('已完成：' + str(page) + "-" + str(page+19))
        page = page + 20
        time.sleep(0.2)

fp.close()
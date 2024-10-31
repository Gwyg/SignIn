
import requests
from bs4 import BeautifulSoup
import sys

def getsessiuon(unicode,password):
    url = 'http://lms.tc.cqupt.edu.cn/user/index' # 学在重邮首页
    headers = {
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36",
    }
    session = requests.session()
    login_html = session.get("http://lms.tc.cqupt.edu.cn/user/index",headers=headers)
    soup = BeautifulSoup(login_html.text, "html.parser")
    execution = soup.find(id="execution").attrs["value"]
    data = {
        'username': unicode,
        'password': password,
        # 'captcha': 123,# 验证码
        '_eventId': 'submit',
        'cllt': 'userNameLogin',
        'dllt': 'generalLogin',
        'execution': execution,
    }
    session.post("https://ids.cqupt.edu.cn/authserver/login", data=data, headers=headers,params=url)
    return session.cookies.get('session')

if __name__ == '__main__':
    if len(sys.argv) > 1:
        arg1 = sys.argv[1]
        arg2 = sys.argv[2]
        print(getsessiuon(arg1,arg2))
    else:
        print("请输入学号和密码")
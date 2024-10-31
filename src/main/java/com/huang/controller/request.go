package CQUPT_SignUp

import (
	"encoding/json"
	"errors"
	"fmt"
	"github.com/go-resty/resty/v2"
)

var (
	client  *resty.Client
	session string
)

type Rollcall struct {
	AvatarBigUrl      string `json:"avatar_big_url"`
	ClassName         string `json:"class_name"`
	CourseID          int    `json:"course_id"`
	CourseTitle       string `json:"course_title"`
	CreatedBy         int    `json:"created_by"`
	CreatedByName     string `json:"created_by_name"`
	DepartmentName    string `json:"department_name"`
	GradeName         string `json:"grade_name"`
	GroupSetID        int    `json:"group_set_id"`
	IsExpired         bool   `json:"is_expired"`
	IsNumber          bool   `json:"is_number"`
	IsRadar           bool   `json:"is_radar"`
	PublishedAt       string `json:"published_at"`
	RollcallID        int    `json:"rollcall_id"`
	RollcallStatus    string `json:"rollcall_status"`
	RollcallTime      string `json:"rollcall_time"`
	Scored            bool   `json:"scored"`
	Source            string `json:"source"`
	Status            string `json:"status"`
	StudentRollcallID int    `json:"student_rollcall_id"`
	Title             string `json:"title"`
	Type              string `json:"type"`
}

type Response struct {
	Rollcalls []Rollcall `json:"rollcalls"`
}

func New(s string) {
	client = resty.New()
	session = s
}

func SignUpAllClasses() error {
	headers := map[string]string{
		"Accept-Language":  "zh-Hans",
		"Host":             "lms.tc.cqupt.edu.cn",
		"Origin":           "http://mobile.tc.cqupt.edu.cn",
		"Referer":          "http://mobile.tc.cqupt.edu.cn/",
		"X-Forwarded-User": "P338kFwtHL4GEPN3",
		"X-Requested-With": "XMLHttpRequest",
		"X-SESSION-ID":     session,
	}

	resp, err := client.R().
		SetHeaders(headers).
		Get("http://lms.tc.cqupt.edu.cn/api/radar/rollcalls?api_version=1.10")

	if err != nil {
		fmt.Println("Error:", err)
		return err
	}

	var res *Response
	err = json.Unmarshal([]byte(resp.String()), &res)
	if err != nil {
		fmt.Println("Error:", err)
		return err
	}

	if len(res.Rollcalls) == 0 {
		fmt.Println("当前没有签到")
		return errors.New("当前没有签到")
	}

	for _, v := range res.Rollcalls {
		if v.Status == "absent" {
			err = signUp(fmt.Sprintf("%d", v.CourseID), fmt.Sprintf("%d", v.RollcallID))
			if err != nil {
				return err
			}
		}
	}
	return nil
}

func getData(courseId, rollCallId string) string {
	headers := map[string]string{
		"Accept-Language":  "zh-CN,zh;q=0.9,en;q=0.8",
		"Host":             "lms.tc.cqupt.edu.cn",
		"Origin":           "http://mobile.tc.cqupt.edu.cn",
		"Referer":          "http://mobile.tc.cqupt.edu.cn/",
		"X-Forwarded-User": "P338kFwtHL4GEPN3",
		"X-SESSION-ID":     session,
	}

	url := fmt.Sprintf("http://lms.tc.cqupt.edu.cn/api/course/%s/rollcall/%s/qr_code", courseId, rollCallId)

	resp, err := client.R().
		SetHeaders(headers).
		Get(url)

	if err != nil {
		fmt.Println("Error:", err)
		return ""
	}

	return resp.String()
}

func signUp(courseId, rollCallId string) error {
	headers := map[string]string{
		"Accept-Language":  "zh-Hans",
		"Host":             "lms.tc.cqupt.edu.cn",
		"Origin":           "http://mobile.tc.cqupt.edu.cn",
		"Referer":          "http://mobile.tc.cqupt.edu.cn/",
		"X-Forwarded-User": "P338kFwtHL4GEPN3",
		"X-Requested-With": "XMLHttpRequest",
		"Content-Type":     "application/json",
		"X-SESSION-ID":     session,
	}

	url := fmt.Sprintf("http://lms.tc.cqupt.edu.cn/api/rollcall/%s/answer_qr_rollcall", rollCallId)

	resp, err := client.R().
		SetHeaders(headers).
		SetBody(getData(courseId, rollCallId)).
		Put(url)

	if err != nil {
		fmt.Println("Error:", err)
		return err
	}

	if resp.Status() != "200 OK" {
		fmt.Println("签到失败:" + resp.String())
		return errors.New("签到失败")
	} else {
		fmt.Println("签到成功")
	}

	return nil
}
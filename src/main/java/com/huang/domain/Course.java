package com.huang.domain;




import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    private String avatarBigUrl;
    private String className;
    private int courseID;
    private String courseTitle;
    private int createdBy;
    private String createdByName;
    private String departmentName;
    private String gradeName;
    private int groupSetID;
    private boolean isExpired;
    private boolean isNumber;
    private boolean isRadar;
    private String publishedAt;
    private int rollcallID;
    private String rollcallStatus;
    private String rollcallTime;
    private boolean scored;
    private String source;
    private String status;
    private int studentRollcallID;
    private String title;
    private String type;
}

package com.sun.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Accessors(chain = true)
public class Step {
    public String CraneNO;
    public String OrderNO;
    public double RecTime;
    public double CurX;
    public double CurY;
    public double CurZ;
}

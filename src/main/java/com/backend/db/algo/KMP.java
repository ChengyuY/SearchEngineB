package com.backend.db.algo;

public class KMP {


      private static int[] calculateNext(String str) {

          int i = -1;
          int j = 0;
          int length = str.length();
          int next[] = new int[length];
          next[0] = -1;

          while (j < length - 1) {
              if (i == -1 || str.charAt(i) == str.charAt(j)) {
                  i++;
                  j++;
                  next[j] = i;
              } else {
                 i = next[i];
              }
          }
          return next;
      }

      public static int match(String source, String pattern) {

          int i = 0;
          int j = 0;
          int input_len = source.length();
          int kw_len = pattern.length();
          int[] next = calculateNext(pattern);

          while ((i < input_len) && (j < kw_len)) {
              // 如果j = -1，或者当前字符匹配成功（即S[i] == P[j]），都令i++，j++
              if (j == -1 || source.charAt(i) == pattern.charAt(j)) {
                  j++;
                  i++;
              } else {
                  // 如果j != -1，且当前字符匹配失败（即S[i] != P[j]），则令 i 不变，j = next[j],
                  // next[j]即为j所对应的next值
                  j = next[j];
              }
          }
          if (j == kw_len) {
              return i - kw_len;
          } else {
              return -1;
          }
      }

 }


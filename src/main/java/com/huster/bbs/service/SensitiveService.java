package com.huster.bbs.service;

import com.huster.bbs.controller.QuestionController;
import org.apache.commons.lang3.CharUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class SensitiveService implements InitializingBean {

    //日志打印
    private final static Logger logger = LoggerFactory.getLogger(SensitiveService.class);

    //实现InitializingBean接口，在bean初始化的时候，实现读取SensitiveWords.txt中的敏感词内容
    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWords.txt");
            InputStreamReader read = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                lineTxt = lineTxt.trim(); // 前后空格处理下
                addWord(lineTxt);
            }
            read.close();
        } catch (Exception e) {
            logger.error("读取敏感词文件失败" + e.getMessage());
        }
    }

    //前缀树的实现，私有内部类
    private class TrieNode {
        //是不是敏感词结尾
        private boolean end = false;
        //当前节点下的所有子节点
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        //添加节点
        void addSubNode(Character key, TrieNode node) { subNodes.put(key, node); }

        //获取子节点
        TrieNode getSubNode(Character key) { return subNodes.get(key); }

        //判断是不是结尾
        boolean isKeyWordEnd() { return end; }

        //初始化结尾
        void setKeyWordEnd(boolean end) { this.end = end; }
    }

    //根节点
    private TrieNode rootNode = new TrieNode();

    // 判断是否是一个非法的字符
    private boolean isSymbol(char c) {
        int ic = (int) c;
        // 0x2E80-0x9FFF 东亚文字范围，不是东亚文字，不是英文就给过滤掉
        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF);
    }

    //过滤敏感词
    public String filter(String text) {
        if(StringUtils.isEmpty(text)) {
            return text;
        }

        StringBuilder result = new StringBuilder();//返回的字符串

        //敏感词替换
        String replacement = "***";

        //三个指针
        TrieNode tempNode = rootNode;
        int begin = 0;//敏感树搜索到的位置
        int position = 0;//当前需要比较的位置

        while (position < text.length()) {
            char c = text.charAt(position);
            // 是非法字符，直接跳过，比如空格、特殊字符等
            if (isSymbol(c)) {
                if (tempNode == rootNode) {
                    result.append(c);
                    ++begin;
                }
                ++position;
                continue;
            }
            tempNode = tempNode.getSubNode(c);
            // 当前位置的匹配结束，没有找到敏感词
            if (tempNode == null) {
                // 以begin开始的字符串不存在敏感词
                result.append(text.charAt(begin));
                // 跳到下一个字符开始测试
                position = begin + 1;
                begin = position;
                // 回到树初始节点
                tempNode = rootNode;
            } else if (tempNode.isKeyWordEnd()) {
                // 发现敏感词，从begin到position的位置用replacement替换掉
                result.append(replacement);
                position = position + 1;
                begin = position;
                tempNode = rootNode;
            } else {
                ++position;
            }
        }
        //最后一段字符串追加至末尾
        result.append(text.substring(begin));
        return result.toString();
    }


    //添加敏感词到树里，每个敏感词，调用一遍这个方法，一条路径往下生成树
    private void addWord(String LineTxt) {
        //根节点
        TrieNode tmpNode = rootNode;
        for (int i = 0; i < LineTxt.length(); i++) {
            Character c = LineTxt.charAt(i);

            if (isSymbol(c)) {//如果构造数的时候，是非法字符，直接跳过
                continue;
            }

            TrieNode node = tmpNode.getSubNode(c);
            if (node == null) {//如果c字符没插入树中，则插入当前节点的子节点中
                node = new TrieNode();
                tmpNode.addSubNode(c, node);
            }
            tmpNode = node;
            if (i == LineTxt.length() - 1) {
                //如果到了结尾
                tmpNode.setKeyWordEnd(true);
            }
        }
    }


}

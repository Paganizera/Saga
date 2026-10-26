import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';

const components = {
    a: ({node, ...props}) => <a {...props} target="_blank" rel="noopener noreferrer nofollow"/>,
};

export default function Markdown({children}) {
    return <ReactMarkdown remarkPlugins={[remarkGfm]} components={components}>{children}</ReactMarkdown>;
}
